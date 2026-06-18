# Análise de Arquitetura — oficinaMecanica

> Atualizado em: 2026-03-16 | Branch: master | Stack: Java 17 + Spring Boot 3 + H2
> Reflete o estado do código após refatoração e correção de todos os gaps identificados.

---

## 1. Arquitetura

Aplicação REST monolítica em arquitetura **em camadas** (Controller → Service → Repository → Entity).
Usa Spring Boot 3 com Spring Data JPA (Hibernate), banco H2 em memória ativado pelo profile `test`,
e JPA Auditing habilitado (`@EnableJpaAuditing`) para registro automático de timestamps.
Cada recurso (`Cliente`, `Carro`, `Produto`, `Servico`, `Order`) possui Controller, Service e
Repository próprios — separação de responsabilidades consistente em todas as camadas.
DTOs de resposta (`*ResponseDto`) e de entrada (`OrderRequestDto`) isolam o domínio da API.
Não há camada de segurança, cache, filas ou serviços externos.

```
HTTP
 │
 ▼
[Controller]  ── @Valid ──► validação de entrada (Jakarta Validation)
 │
 ▼
[Service]  ── @Transactional ──► regras de negócio + lookup de entidades relacionadas
 │
 ▼
[Repository]  ── Spring Data JPA + @EntityGraph ──► H2 (in-memory, profile "test")
 │
 ▼
[Entity / Domain Model]  ── BigDecimal, Auditing, OrderItem interface
 │
 ▼
[DTO]  ── OrderRequestDto (entrada) / OrderResponseDto (saída)
 │
 ▼
[GlobalExceptionHandler]  ── 404 / 400 / 409 / 500
```

**Componentes principais:**

| Pacote | Responsabilidade |
|---|---|
| `controller/` | Mapeamento HTTP, `@Valid`, montagem de `URI` 201, constructor injection |
| `services/` | Regras de negócio, transações, validação de domínio (ex.: carro × cliente) |
| `repositories/` | Spring Data JPA — CRUD + `@EntityGraph` para fetch eager em `Order` |
| `entities/` | Modelo de domínio JPA; `Order` com auditoria e `getTotal()` em `BigDecimal` |
| `entities/OrderItem` | Interface de contrato para itens heterogêneos da OS |
| `dto/` | `OrderRequestDto` (entrada), `OrderResponseDto` (saída), `ErrorResponse` |
| `exceptions/` | `ResourceNotFoundException` + `GlobalExceptionHandler` (404/400/409/500) |
| `config/TestConfig` | Seed de dados para o profile `test` |

---

## 2. Fluxo Principal — Criação de uma Ordem de Serviço (POST /orders)

```
1. Cliente HTTP  →  POST /orders
   Body: { "status": "RECEBIDO", "clienteId": 1, "carroId": 1 }

2. OrderController.insert()                         controller/OrderController.java:38
   └─ @Valid valida OrderRequestDto                 dto/OrderRequestDto.java
      └─ status: @NotNull
      └─ clienteId: @NotNull

3. OrderService.insert(OrderRequestDto)             services/OrderService.java:44
   ├─ clienteRepository.findById(clienteId)         → 404 se não existir
   ├─ new Order() + setCliente() + setOrderStatus()
   │     └─ OrderStatus.fromCode(int)               enums/OrderStatus.java:22
   ├─ [se carroId != null]
   │   ├─ carroRepository.findById(carroId)         → 404 se não existir
   │   ├─ validarCarroPertenceAoCliente()            services/OrderService.java:109
   │   │     └─ carro.cliente.id == order.cliente.id → 400 se divergir
   │   └─ order.setCarro(carro)
   └─ orderRepository.save(order)
      └─ @CreatedDate / @LastModifiedDate preenchidos automaticamente

4. new OrderResponseDto(order)                      dto/OrderResponseDto.java:26
   ├─ order.getOrderStatus()  → OrderStatus.fromCode(orderStatus)
   ├─ order.getTotal()        → BigDecimal soma de serviços + produtos
   ├─ order.getItens()        → List<OrderItem> (OrderProduto + OrderServico)
   ├─ order.getCreatedAt()    → Instant (auditoria)
   └─ order.getUpdatedAt()    → Instant (auditoria)

5. ResponseEntity.created(uri).body(response)  →  HTTP 201 + Location header
```

**Fluxo de leitura com itens (GET /orders/{id}):**

```
OrderController.findById()
  → OrderService.findById()                   services/OrderService.java:31
  → orderRepository.findWithItensById(id)     repositories/OrderRepository.java
        └─ @EntityGraph(orderServicos, orderProdutos)  ← fetch eager, sem LazyInit
  → new OrderResponseDto(order)
        └─ order.getTotal()  ←  calcula sobre coleções já carregadas
```

**Fluxo de Atualização (PUT /orders/{id}):**

```
OrderController.update()
  → OrderService.updateData()                 services/OrderService.java:81
    ├─ Se clienteId mudou  → rebusca Cliente
    ├─ Se carroId mudou    → rebusca Carro + valida pertencimento ao cliente
    └─ Atualiza status + salva
       └─ @LastModifiedDate atualizado automaticamente
```

---

## 3. Regras de Negócio Implementadas

| # | Regra | Implementação |
|---|---|---|
| RN-01 | Nome do cliente obrigatório, entre 3 e 100 caracteres | `entities/Cliente.java:26–27` |
| RN-02 | Telefone com 10 ou 11 dígitos numéricos | `entities/Cliente.java:29` |
| RN-03 | Email válido e obrigatório | `entities/Cliente.java:32–34` |
| RN-04 | Documento obrigatório, 11 dígitos (CPF) ou 14 dígitos (CNPJ), único no sistema | `entities/Cliente.java:36–39` `@NotBlank @Pattern @Column(unique)` |
| RN-05 | Placa no formato `ABC-1234`, única no sistema | `entities/Carro.java:24–26` `@Pattern @Column(unique)` |
| RN-06 | Preço de produto e serviço deve ser positivo, com precisão de 2 casas decimais | `entities/Produto.java:27–29`, `entities/Servico.java:28–30` |
| RN-07 | Quantidade de produto não pode ser negativa | `entities/Produto.java:31–32` `@PositiveOrZero` |
| RN-08 | Status da OS segue ciclo numérico 1–6 (RECEBIDO → ENTREGUE) | `entities/enums/OrderStatus.java` + `entities/Order.java:69` |
| RN-09 | OS sempre exige um Cliente | `dto/OrderRequestDto.java:14` + `services/OrderService.java:47` |
| RN-10 | Carro é opcional na OS | `services/OrderService.java:52` `if (carroId != null)` |
| RN-11 | Carro associado à OS deve pertencer ao mesmo Cliente da OS | `services/OrderService.java:109–115` `validarCarroPertenceAoCliente()` |
| RN-12 | Subtotal do item = preço × quantidade (com guarda contra null) | `entities/OrderProduto.java:95–98` `getSubTotal()` |
| RN-13 | Total da OS = soma dos subtotais de produtos + preços de serviços | `entities/Order.java:121–130` `getTotal()` em `BigDecimal` |
| RN-14 | Busca de cliente por documento (CPF/CNPJ) | `repositories/ClienteRepository.java:11` + `services/ClienteService.java:41` |
| RN-15 | Status da OS obrigatório na criação e atualização | `dto/OrderRequestDto.java:13` `@NotNull` |
| RN-16 | Preços são copiados (snapshot) no momento da inclusão do item na OS | `entities/OrderProduto.java:35–45`, `entities/OrderServico.java:37–47` |
| RN-17 | Data de criação e última modificação da OS registradas automaticamente | `entities/Order.java:27–32` `@CreatedDate @LastModifiedDate` |

---

## 4. Estado Atual — Gaps Corrigidos

Todos os gaps identificados na versão anterior foram corrigidos:

| Gap | Status | Solução aplicada |
|---|---|---|
| G-01 LazyInitializationException em GET /orders | ✅ Corrigido | `@EntityGraph` + `@Query` em `OrderRepository` |
| G-02 Carro de outro cliente associado à OS | ✅ Corrigido | `validarCarroPertenceAoCliente()` em `OrderService` |
| G-03 `doc` sem validação de formato | ✅ Corrigido | `@Pattern(\\d{11}\|\\d{14})` em `Cliente.doc` |
| G-04 `doc` duplicado aceito | ✅ Corrigido | `@Column(unique=true)` em `Cliente.doc` |
| G-05 `placa` duplicada aceita | ✅ Corrigido | `@Column(unique=true)` em `Carro.placa` |
| G-06 Produto não pode aparecer 2×na mesma OS | ⚠️ Limitação arquitetural | Chave composta `(orderId, produtoId)` é intencional; endpoint de atualização de quantidade deve ser implementado futuramente |
| G-07 `IllegalArgumentException` para status inválido | ✅ Já tratado | `GlobalExceptionHandler` captura → 400 |
| G-08 Deleção com dependências retornava 500 | ✅ Corrigido | Handler `DataIntegrityViolationException` → 409 Conflict |
| G-09 Dois clientes com `doc` idêntico no seed | ✅ Corrigido | `TestConfig` usa CPFs únicos e válidos |
| G-10 `BigDecimal` sem `precision/scale` | ✅ Corrigido | `@Column(precision=10, scale=2)` nos 4 campos monetários |
| G-11 OS sem timestamps de auditoria | ✅ Corrigido | `@CreatedDate`/`@LastModifiedDate` + `@EnableJpaAuditing` |
| G-12 `List<Object>` como tipo de itens | ✅ Corrigido | Interface `OrderItem`; `getItens()` retorna `List<OrderItem>` |
| G-13 `Carro.equals()` usava `id + placa` | ✅ Corrigido | `equals`/`hashCode` baseados somente em `id` |

**Bônus corrigidos:**

| Item | Solução |
|---|---|
| `ServicoController.delete()` retornava HTTP 200 | Corrigido para `ResponseEntity<Void>` 204 No Content |
| `@Autowired` em campos em todos os controllers e services | Substituído por constructor injection |
| `OrderResponseDto` usado como entrada no POST/PUT | Separado em `OrderRequestDto` (entrada) e `OrderResponseDto` (saída) |
| `Double` para valores monetários | Substituído por `BigDecimal` em todas as entidades |
| `OrderStatus.valueOf(int)` colide com `Enum.valueOf(String)` | Renomeado para `fromCode(int)` |

---

## 5. Pontos de Atenção Restantes (Backlog Futuro)

Estes itens **não são bugs** — são melhorias arquiteturais para versões futuras:

| # | Item | Esforço | Prioridade |
|---|---|---|---|
| F-01 | Expor entidades JPA diretamente em `Cliente`, `Carro`, `Produto`, `Servico` — idealmente usariam DTOs de entrada/saída também | Médio | Alta |
| F-02 | Validação dos dígitos verificadores de CPF e CNPJ (além do formato numérico) | Rápido | Alta |
| F-03 | Endpoint dedicado para adicionar/remover itens de uma OS (`POST /orders/{id}/produtos`, `POST /orders/{id}/servicos`) | Grande | Alta |
| F-04 | Paginação em todos os `findAll()` com `Pageable` | Rápido | Média |
| F-05 | `@Version` (optimistic locking) em `Order` para evitar lost updates concorrentes | Rápido | Alta |
| F-06 | Migração de H2 para PostgreSQL com Flyway para controle de schema | Médio | Alta |
| F-07 | Autenticação e autorização (Spring Security + JWT) | Grande | Alta |
| F-08 | Endpoint de transição de status da OS com validação de fluxo (ex.: não permitir ir de ENTREGUE para RECEBIDO) | Médio | Alta |

---

## 6. Estrutura de Arquivos Atual

```
src/main/java/com/projetoweb/oficinamecanica/
├── OficinaMecanicaApplication.java          ← @SpringBootApplication @EnableJpaAuditing
├── config/
│   └── TestConfig.java                      ← Seed de dados (profile "test")
├── controller/
│   ├── ClienteController.java
│   ├── CarroController.java
│   ├── ProdutoController.java
│   ├── ServicoController.java
│   └── OrderController.java
├── dto/
│   ├── OrderRequestDto.java                 ← Entrada: status, clienteId, carroId
│   ├── OrderResponseDto.java                ← Saída: inclui total, itens, timestamps
│   ├── ClienteResponseDto.java
│   ├── CarroResponseDto.java
│   └── ErrorResponse.java
├── entities/
│   ├── OrderItem.java                       ← Interface: getNome(), getPreco()
│   ├── Cliente.java                         ← doc: unique + @Pattern CPF/CNPJ
│   ├── Carro.java                           ← placa: unique; equals por id
│   ├── Produto.java                         ← preco: BigDecimal precision(10,2)
│   ├── Servico.java                         ← preco: BigDecimal precision(10,2)
│   ├── Order.java                           ← getTotal(): BigDecimal; auditoria
│   ├── OrderProduto.java                    ← implements OrderItem; getSubTotal()
│   ├── OrderServico.java                    ← implements OrderItem; equals/hashCode
│   ├── enums/
│   │   └── OrderStatus.java                 ← fromCode(int) — RECEBIDO..ENTREGUE
│   └── pk/
│       ├── OrderProdutoPK.java
│       └── OrderServicoPK.java
├── exceptions/
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java          ← 404 / 400 / 409 / 500 + logging
├── repositories/
│   ├── ClienteRepository.java               ← findByDoc(String)
│   ├── CarroRepository.java
│   ├── ProdutoRepository.java
│   ├── ServicoRepository.java
│   ├── OrderRepository.java                 ← @EntityGraph findWithItensById/findAllWithItens
│   ├── OrderProdutoRepository.java
│   └── OrderServicoRepository.java
└── services/
    ├── ClienteService.java
    ├── CarroService.java
    ├── ProdutoService.java
    ├── ServicoService.java
    └── OrderService.java                    ← validarCarroPertenceAoCliente()

src/test/java/com/projetoweb/oficinamecanica/
├── OficinaMecanicaApplicationTests.java
└── config/
    └── TestConfigTest.java                  ← 17 testes — todos passando ✅
```

---

## 7. Suposições

1. O banco de destino em produção será PostgreSQL ou MySQL — H2 é apenas para dev/test.
2. Não existe autenticação — assume-se API interna ou protegida por API Gateway.
3. A gestão de itens da OS (adicionar produto/serviço) ocorre diretamente via
   `OrderProdutoRepository`/`OrderServicoRepository`; não há controller REST dedicado ainda.
4. Validação de dígitos verificadores de CPF/CNPJ está fora do escopo atual — apenas o
   formato numérico (11 ou 14 dígitos) é verificado.