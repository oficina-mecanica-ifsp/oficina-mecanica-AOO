# Fluxograma — Oficina Mecânica API

```mermaid
flowchart TD

    %% ─────────────────────────────────────────
    %% ENTRY POINT
    %% ─────────────────────────────────────────
    REQ([fa:fa-globe Requisição HTTP]) --> FILTER

    %% ─────────────────────────────────────────
    %% SEGURANÇA
    %% ─────────────────────────────────────────
    subgraph SEC["🔐 Segurança  —  JwtAuthenticationFilter"]
        FILTER{"JWT no header\nAuthorization: Bearer?"}
        FILTER -->|Ausente ou inválido| R401(["❌ 401 Unauthorized"])
        FILTER -->|Válido| CTXT["Popula SecurityContext\nusuário + ROLE"]
    end

    CTXT --> ROUTER

    %% ─────────────────────────────────────────
    %% ROTEAMENTO POR ENDPOINT
    %% ─────────────────────────────────────────
    subgraph ROUTE["🗺️ Roteamento"]
        ROUTER{Endpoint chamado}
        ROUTER -->|POST /auth/login\npermitAll| EP_LOGIN["AuthController\n.login()"]
        ROUTER -->|POST /auth/cadastrar\n🔒 ADMIN| EP_REG["AuthController\n.cadastrar()"]
        ROUTER -->|CRUD /clientes /carros\n/servicos /produtos\n🔒 ATENDENTE + ADMIN| EP_CAD["Controllers\nde Cadastro"]
        ROUTER -->|POST /orders\n🔒 ATENDENTE + ADMIN| EP_OS["OrderController\n.insert()"]
        ROUTER -->|POST /orders/{id}/produtos\nPOST /orders/{id}/servicos\n🔒 MECANICO + ADMIN| EP_ITEM["OrderController\n.adicionarProduto/Servico()"]
        ROUTER -->|PATCH /orders/{id}/status\n🔒 MECANICO + ADMIN| EP_STATUS["OrderController\n.atualizarStatus()"]
        ROUTER -->|POST /pagamentos\n🔒 autenticado| EP_PAG["PagamentoController\n.registrar()"]
        ROUTER -->|GET /produtos/estoque-critico\n🔒 autenticado| EP_CRIT["ProdutoController\n.findEstoqueCritico()"]
    end

    %% ─────────────────────────────────────────
    %% AUTENTICAÇÃO
    %% ─────────────────────────────────────────
    subgraph AUTH["🔑 Autenticação  —  UsuarioService"]
        EP_LOGIN --> VAL_CRED{"Email + senha\ncorrespondentes?\nBCrypt.matches"}
        VAL_CRED -->|Não| R401b(["❌ 401 Unauthorized"])
        VAL_CRED -->|Sim| GEN_JWT["JwtUtil.gerarToken()\nHS256  •  exp: 24h"]
        GEN_JWT --> RESP_JWT(["✅ 200 LoginResponseDto\n{ token, email, role }"])

        EP_REG --> CHK_EMAIL{"E-mail\njá existe?"}
        CHK_EMAIL -->|Sim| R422_email(["❌ 422 E-mail já cadastrado"])
        CHK_EMAIL -->|Não| HASH_PWD["BCrypt.encode(senha)\nSalva Usuário"]
        HASH_PWD --> RESP_USER(["✅ 201 UsuarioResponseDto"])
    end

    %% ─────────────────────────────────────────
    %% CADASTROS
    %% ─────────────────────────────────────────
    subgraph CADS["📋 Cadastros"]
        EP_CAD -->|POST /clientes| SAVE_CLI(["✅ 201 Cliente salvo"])
        EP_CAD -->|POST /carros| VAL_CARRO{"Carro pertence\nao cliente?"}
        VAL_CARRO -->|Não| R422_carro(["❌ 422 Carro não pertence ao cliente"])
        VAL_CARRO -->|Sim| SAVE_CARRO(["✅ 201 Carro salvo"])

        EP_CAD -->|POST /produtos| SAVE_PROD(["✅ 201 Produto salvo\ntipo: PECA | PRODUTO\nqtdMinima definida"])
        EP_CAD -->|POST /produtos/{id}/entrada| INC_STOCK["EstoqueService\n.registrarEntrada()\nquantidade += dto.quantidade"]
        INC_STOCK --> RESP_ENTRADA(["✅ 200 ProdutoResponseDto"])
    end

    %% ─────────────────────────────────────────
    %% CRIAR OS
    %% ─────────────────────────────────────────
    subgraph OS_CREATE["📄 Criar Ordem de Serviço"]
        EP_OS --> VAL_CLI{"Cliente\nexiste?"}
        VAL_CLI -->|Não| R404_cli(["❌ 404 Cliente não encontrado"])
        VAL_CLI -->|Sim| VAL_CARRO2{"carroId\ninformado?"}
        VAL_CARRO2 -->|Sim| VAL_CARRO3{"Carro pertence\nao cliente?"}
        VAL_CARRO3 -->|Não| R422_os(["❌ 422 Regra de negócio"])
        VAL_CARRO3 -->|Sim| OS_RECEBIDO
        VAL_CARRO2 -->|Não| OS_RECEBIDO(["✅ 201  OS criada\nstatus: RECEBIDO"])
    end

    %% ─────────────────────────────────────────
    %% ADICIONAR ITENS
    %% ─────────────────────────────────────────
    subgraph ITEM_FLOW["🔧 Adicionar Itens à OS"]
        EP_ITEM -->|produto / peça| CHK_DUP{"Produto já\nna OS?"}
        CHK_DUP -->|Sim| R422_dup(["❌ 422 Produto já adicionado"])
        CHK_DUP -->|Não| CHK_STOCK{"Estoque ≥\nquantidade solicitada?"}
        CHK_STOCK -->|Não| R422_stock(["❌ 422 Estoque insuficiente"])
        CHK_STOCK -->|Sim| DECR["EstoqueService.decrementar()\nquantidade -= dto.quantidade\nSnapshot salvo em OrderProduto"]
        DECR --> CHK_MIN{"qty atual <\nqtdMinima?"}
        CHK_MIN -->|Sim| EMAIL_ALERT["📧 EmailService\n.enviarAlertaEstoque()\npara admin@oficina.com"]
        CHK_MIN -->|Não| ITEM_DONE
        EMAIL_ALERT --> ITEM_DONE(["✅ 200 OS atualizada\nTotal = Σ subTotal dos itens"])

        EP_ITEM -->|serviço| CHK_SVC_DUP{"Serviço já\nna OS?"}
        CHK_SVC_DUP -->|Sim| R422_svc(["❌ 422 Serviço já adicionado"])
        CHK_SVC_DUP -->|Não| SVC_SNAP["Snapshot salvo\nem OrderServico"]
        SVC_SNAP --> ITEM_DONE
    end

    %% ─────────────────────────────────────────
    %% ATUALIZAR STATUS
    %% ─────────────────────────────────────────
    subgraph STATUS_FLOW["📊 Atualizar Status da OS"]
        EP_STATUS --> CHK_TRANS{"statusAtual\n.canTransitionTo\n(novoStatus)?"}
        CHK_TRANS -->|Não| R422_trans(["❌ 422 Transição inválida\nex: RECEBIDO → ENTREGUE"])
        CHK_TRANS -->|Sim| CHK_VALIDADE{"novoStatus =\nAGUARDANDO_APROVACAO\ne sem dataValidade?"}
        CHK_VALIDADE -->|Sim| R422_val(["❌ 422 dataValidade obrigatória\npara orçamento"])
        CHK_VALIDADE -->|Não| CHK_FINPAG{"novoStatus =\nFINALIZADO ou ENTREGUE?"}
        CHK_FINPAG -->|Sem pagamento| R422_pag(["❌ 422 Registrar pagamento\nantes de finalizar"])
        CHK_FINPAG -->|Com pagamento| APPLY_STATUS["Aplica novoStatus\nsalva Order"]
        CHK_FINPAG -->|Outro status| APPLY_STATUS
        APPLY_STATUS --> CHK_CHANGED{"Status\nmudou?"}
        CHK_CHANGED -->|Não| RESP_STATUS2(["✅ 200 OrderResponseDto"])
        CHK_CHANGED -->|Sim| EMAIL_STATUS["📧 EmailService\n.enviarAtualizacaoStatus()\npara email do cliente"]
        EMAIL_STATUS --> RESP_STATUS2
    end

    %% ─────────────────────────────────────────
    %% WORKFLOW DE STATUS (referência)
    %% ─────────────────────────────────────────
    subgraph WORKFLOW["🔄 Workflow de Transições Permitidas"]
        direction LR
        W1([RECEBIDO]) -->|mecânico diagnostica| W2([EM_DIAGNOSTICO])
        W2 -->|gera orçamento| W3([AGUARDANDO\nAPROVACAO])
        W3 -->|cliente aprova| W4([EM_EXECUCAO])
        W4 -->|trabalho concluído| W5([FINALIZADO])
        W5 -->|cliente retira| W6([ENTREGUE])
        W1 & W2 & W3 & W4 -->|prazo vencido| W7([EM_ATRASO])
        W7 -->|retomada| W4
    end

    %% ─────────────────────────────────────────
    %% PAGAMENTO
    %% ─────────────────────────────────────────
    subgraph PAG_FLOW["💰 Registrar Pagamento"]
        EP_PAG --> CHK_OS_EX{"OS\nexiste?"}
        CHK_OS_EX -->|Não| R404_os(["❌ 404 OS não encontrada"])
        CHK_OS_EX -->|Já tem pagamento| R422_jpag(["❌ 422 Pagamento já registrado"])
        CHK_OS_EX -->|OK| SAVE_PAG["Salva Pagamento\n{ valor, FormaPagamento }\nDINHEIRO | PIX | CARTAO_CREDITO\nCARTAO_DEBITO | TRANSFERENCIA | BOLETO"]
        SAVE_PAG --> LIBERA(["✅ 201 PagamentoResponseDto\nFINALIZADO e ENTREGUE liberados"])
    end

    %% ─────────────────────────────────────────
    %% ESTOQUE CRÍTICO
    %% ─────────────────────────────────────────
    EP_CRIT --> QUERY_CRIT["ProdutoRepository\n.findEstoqueCritico()\nWHERE quantidade < quantidadeMinima"]
    QUERY_CRIT --> RESP_CRIT(["✅ 200 Page<ProdutoResponseDto>"])

    %% ─────────────────────────────────────────
    %% ESTILOS
    %% ─────────────────────────────────────────
    classDef error fill:#ffd6d6,stroke:#d32f2f,color:#b71c1c
    classDef success fill:#d4edda,stroke:#388e3c,color:#1b5e20
    classDef process fill:#e3f2fd,stroke:#1565c0,color:#0d47a1
    classDef decision fill:#fff9c4,stroke:#f9a825,color:#e65100

    class R401,R401b,R422_email,R422_carro,R422_os,R404_cli,R422_dup,R422_stock,R422_svc,R422_trans,R422_val,R422_pag,R404_os,R422_jpag,R422_stock error
    class RESP_JWT,RESP_USER,SAVE_CLI,SAVE_CARRO,SAVE_PROD,RESP_ENTRADA,OS_RECEBIDO,ITEM_DONE,RESP_STATUS2,LIBERA,RESP_CRIT success
```

---

## Resumo dos Fluxos

| Fluxo | Role necessário | Endpoints |
|---|---|---|
| Login | Público | `POST /auth/login` |
| Criar usuário | ADMIN | `POST /auth/cadastrar` |
| Cadastrar cliente / carro / serviço / produto | ATENDENTE, ADMIN | `POST /clientes`, `/carros`, `/servicos`, `/produtos` |
| Entrada de estoque | ATENDENTE, ADMIN | `POST /produtos/{id}/entrada` |
| Abrir OS | ATENDENTE, ADMIN | `POST /orders` |
| Adicionar produto/peça/serviço na OS | MECANICO, ADMIN | `POST /orders/{id}/produtos`, `/servicos` |
| Atualizar status da OS | MECANICO, ADMIN | `PATCH /orders/{id}/status` |
| Registrar pagamento | Qualquer autenticado | `POST /pagamentos` |
| Consultar estoque crítico | Qualquer autenticado | `GET /produtos/estoque-critico` |

## Regras de Negócio Aplicadas Automaticamente

- OS sempre abre com status **RECEBIDO**
- Total da OS = soma dos `subTotal` de cada item (produto × quantidade + serviço × 1)
- Estoque é decrementado ao adicionar produto; incrementado ao remover
- Email de alerta enviado ao admin quando estoque cai abaixo do mínimo
- Email de atualização enviado ao cliente a cada mudança de status
- **FINALIZADO** e **ENTREGUE** exigem pagamento registrado (422 caso contrário)
- **AGUARDANDO_APROVACAO** exige `dataValidade` no body (422 caso contrário)
- Transições de status seguem matriz fixa — qualquer desvio retorna 422
