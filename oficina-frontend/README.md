# Oficina Mecânica — Frontend IFSP

React 18 + Vite + TailwindCSS + Zustand + React Hook Form + Zod

## Pré-requisitos

- Node.js 18+
- Back-end rodando em `http://localhost:8080`

## Instalação

```bash
cd oficina-frontend
npm install
```

## Configuração

Edite o arquivo `.env` se o back-end estiver em outra porta:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Desenvolvimento

```bash
npm run dev
# Acesse http://localhost:5173
```

## Build de produção

```bash
npm run build
npm run preview
```

---

## Estrutura de pastas

```
src/
├── components/
│   ├── ui/          # Button, Input, Modal, Table, Badge, Alert, Pagination
│   ├── layout/      # AppLayout, Sidebar, Topbar
│   └── guards/      # ProtectedRoute, RoleGuard
├── pages/
│   ├── auth/        # LoginPage
│   ├── clientes/    # ClientesPage, ClienteForm
│   ├── carros/      # CarrosPage, CarroForm
│   ├── ordens/      # OrdensPage, OrdemDetalhePage, OrdemForm, AdicionarItemModal
│   ├── produtos/    # ProdutosPage, ProdutoForm, EntradaEstoqueModal, EstoqueCriticoPage
│   ├── servicos/    # ServicosPage, ServicoForm
│   └── usuarios/    # UsuariosPage
├── services/        # api.js + *Service.js (axios)
├── store/           # authStore.js (Zustand + persist)
├── hooks/           # useAuth, usePagination
└── utils/           # constants.js, formatters.js
```

## Permissões por tela

| Tela / Ação                    | ADMIN | ATENDENTE | MECÂNICO |
|-------------------------------|-------|-----------|----------|
| Login                          | ✓     | ✓         | ✓        |
| Ver ordens                     | ✓     | ✓         | ✓        |
| Abrir / editar OS              | ✓     | ✓         | —        |
| Adicionar item / status OS     | ✓     | —         | ✓        |
| Registrar pagamento            | ✓     | ✓         | —        |
| CRUD Clientes / Carros         | ✓     | ✓         | ✓        |
| Criar / editar Produto         | ✓     | ✓         | —        |
| Excluir Produto                | ✓     | —         | —        |
| Entrada de Estoque             | ✓     | ✓         | —        |
| Estoque Crítico                | ✓     | ✓         | —        |
| Usuários                       | ✓     | —         | —        |

## Máquina de estados da OS

```
RECEBIDO → EM_DIAGNOSTICO | EM_ATRASO
EM_DIAGNOSTICO → AGUARDANDO_APROVACAO | EM_ATRASO
AGUARDANDO_APROVACAO → EM_EXECUCAO | EM_ATRASO  (+ dataValidade obrigatória)
EM_EXECUCAO → FINALIZADO | EM_ATRASO
FINALIZADO → ENTREGUE  (exige pagamento)
EM_ATRASO → EM_EXECUCAO
ENTREGUE → (terminal)
```

## Resposta esperada da API de login

```json
{ "token": "...", "email": "user@email.com", "role": "ADMIN" }
```

## Paginação (Spring Page)

A API deve retornar:
```json
{ "content": [...], "totalElements": 42, "totalPages": 5, "number": 0, "size": 10 }
```
