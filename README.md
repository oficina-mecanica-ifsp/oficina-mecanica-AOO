# Oficina Mecânica IFSP

Sistema de gerenciamento de oficina mecânica desenvolvido como projeto acadêmico para o **Instituto Federal de São Paulo (IFSP)**. A aplicação cobre o ciclo completo de atendimento: cadastro de clientes e veículos, abertura de ordens de serviço, controle de estoque, registro de pagamentos e gestão de usuários com controle de acesso por perfil.

---

## Tecnologias

### Backend
| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.7 |
| Spring Security + JWT (JJWT) | 0.12.6 |
| Spring Data JPA + Hibernate | — |
| MySQL | 8+ |
| Maven | 3.9+ |
| Spring Mail (Gmail SMTP) | — |

### Frontend
| Tecnologia | Versão |
|---|---|
| React | 18.3 |
| Vite | 5.3 |
| React Router DOM | 6.23 |
| Axios | 1.7 |
| Zustand | 4.5 |
| React Hook Form + Zod | 7.51 / 3.23 |
| Tailwind CSS | 3.4 |

---

## Estrutura do Repositório

```
/
├── oficinaMecanica-IFSP/   # Backend Spring Boot
└── oficina-frontend/       # Frontend React + Vite
```

---

## Pré-requisitos

- Java 17+
- Maven 3.9+
- Node.js 18+
- MySQL 8+

---

## Como Rodar Localmente

### 1. Banco de Dados

```sql
CREATE DATABASE mecanica_IFSP;
```

### 2. Backend

```bash
cd oficinaMecanica-IFSP

# Edite src/main/resources/application.properties
# e ajuste as credenciais do MySQL se necessário:
# spring.datasource.username=root
# spring.datasource.password=sua_senha

mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

Na primeira execução, o `DataInitializer` cria automaticamente um usuário administrador:

| Campo | Valor |
|---|---|
| E-mail | admin@oficina.com |
| Senha | admin123 |

### 3. Frontend

```bash
cd oficina-frontend
npm install
npm run dev
```

O frontend sobe em `http://localhost:5173` e já está configurado para se comunicar com o backend em `http://localhost:8080`.

---

## Funcionalidades Implementadas

### Autenticação e Controle de Acesso
- Login com e-mail e senha, autenticação via JWT (Bearer Token)
- Três perfis de acesso com permissões distintas:
  - **ADMIN** — acesso total ao sistema
  - **ATENDENTE** — gerencia clientes, veículos, produtos, ordens e pagamentos
  - **MECÂNICO** — visualiza e atualiza ordens de serviço e adiciona/remove itens

### Clientes
- Cadastro, edição e exclusão de clientes
- Busca por CPF ou CNPJ
- Campos: nome, e-mail, telefone, documento (CPF/CNPJ)
- Listagem paginada com ordenação por nome

### Veículos
- Cadastro, edição e exclusão de veículos vinculados a clientes
- Busca de cliente por CPF/CNPJ no formulário de cadastro com exibição do nome em tempo real
- Campos: placa (formato ABC-1234), modelo, marca, ano de fabricação, cor
- Listagem com paginação

### Ordens de Serviço
- Abertura de OS vinculada a cliente e veículo (opcional)
- Fluxo de status com máquina de estados:
  - `ABERTO` → `EM_ANDAMENTO` ou `EM_ATRASO`
  - `EM_ANDAMENTO` → `FINALIZADO` ou `EM_ATRASO`
  - `EM_ATRASO` → `EM_ANDAMENTO`
  - `FINALIZADO` (estado terminal)
- Pipeline visual de progresso na tela de detalhe
- Adição e remoção de produtos (com quantidade) e serviços à OS
- Visualização de dados do cliente e veículo na OS
- Cálculo automático do total da OS
- Filtro por status na listagem
- Listagem paginada

### Produtos e Estoque
- Cadastro, edição e exclusão de produtos
- Controle de estoque com entrada de quantidade
- Alertas de estoque crítico (abaixo do mínimo configurado)
- Tela dedicada de estoque crítico (ADMIN e ATENDENTE)
- Listagem paginada

### Serviços
- Cadastro, edição e exclusão de serviços
- Campos: nome, preço, duração (horas e minutos)
- Listagem completa

### Pagamentos
- Registro de pagamento por OS com valor e forma de pagamento
- Formas disponíveis: Dinheiro, PIX, Cartão de Crédito, Cartão de Débito, Transferência
- Exibição do status de pagamento na tela de detalhe da OS
- Consulta de pagamento por OS

### Usuários
- Cadastro de novos usuários pelo ADMIN
- Listagem de todos os usuários cadastrados
- Perfis disponíveis: ADMIN, ATENDENTE, MECÂNICO

---

## Endpoints da API

### Auth
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/auth/login` | Público | Autenticação, retorna JWT |
| POST | `/auth/cadastrar` | ADMIN | Cadastrar novo usuário |

### Usuários
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/usuarios` | ADMIN | Listar todos os usuários |

### Clientes
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/clientes` | Autenticado | Listar clientes (paginado) |
| GET | `/clientes/{id}` | Autenticado | Buscar por ID |
| GET | `/clientes/doc/{doc}` | Autenticado | Buscar por CPF/CNPJ |
| POST | `/clientes` | Autenticado | Cadastrar cliente |
| PUT | `/clientes/{id}` | Autenticado | Atualizar cliente |
| DELETE | `/clientes/{id}` | Autenticado | Excluir cliente |

### Veículos
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/carros` | Autenticado | Listar veículos (paginado) |
| GET | `/carros/{id}` | Autenticado | Buscar por ID |
| GET | `/clientes/{id}/carros` | Autenticado | Listar veículos de um cliente |
| POST | `/carros` | Autenticado | Cadastrar veículo |
| PUT | `/carros/{id}` | Autenticado | Atualizar veículo |
| DELETE | `/carros/{id}` | Autenticado | Excluir veículo |

### Ordens de Serviço
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/orders` | Autenticado | Listar ordens (paginado) |
| GET | `/orders/{id}` | Autenticado | Buscar por ID |
| POST | `/orders` | ATENDENTE, ADMIN | Abrir nova OS |
| PUT | `/orders/{id}` | ATENDENTE, ADMIN | Atualizar OS |
| PATCH | `/orders/{id}/status` | MECÂNICO, ADMIN | Avançar status |
| DELETE | `/orders/{id}` | ADMIN | Excluir OS |
| POST | `/orders/{id}/produtos` | MECÂNICO, ADMIN | Adicionar produto à OS |
| DELETE | `/orders/{id}/produtos/{produtoId}` | MECÂNICO, ADMIN | Remover produto da OS |
| POST | `/orders/{id}/servicos` | MECÂNICO, ADMIN | Adicionar serviço à OS |
| DELETE | `/orders/{id}/servicos/{servicoId}` | MECÂNICO, ADMIN | Remover serviço da OS |

### Produtos
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/produtos` | Autenticado | Listar produtos (paginado) |
| GET | `/produtos/{id}` | Autenticado | Buscar por ID |
| GET | `/produtos/estoque-critico` | Autenticado | Listar estoque crítico |
| POST | `/produtos` | ATENDENTE, ADMIN | Cadastrar produto |
| PUT | `/produtos/{id}` | ATENDENTE, ADMIN | Atualizar produto |
| POST | `/produtos/{id}/entrada` | ATENDENTE, ADMIN | Registrar entrada de estoque |
| DELETE | `/produtos/{id}` | ADMIN | Excluir produto |

### Serviços
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/servicos` | Autenticado | Listar serviços |
| GET | `/servicos/{id}` | Autenticado | Buscar por ID |
| POST | `/servicos` | ATENDENTE, ADMIN | Cadastrar serviço |
| PUT | `/servicos/{id}` | ATENDENTE, ADMIN | Atualizar serviço |
| DELETE | `/servicos/{id}` | ADMIN | Excluir serviço |

### Pagamentos
| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/pagamentos` | ATENDENTE, ADMIN | Registrar pagamento |
| GET | `/pagamentos/order/{orderId}` | Autenticado | Buscar pagamento da OS |

---

## Variáveis de Ambiente (Backend)

| Variável | Padrão | Descrição |
|---|---|---|
| `MAIL_USERNAME` | — | E-mail Gmail para envio |
| `MAIL_PASSWORD` | — | Senha de app Gmail |
| `ADMIN_EMAIL` | admin@oficina.com | E-mail do admin inicial |
| `JWT_SECRET` | *(hardcoded)* | Chave de assinatura JWT |
| `JWT_EXPIRATION` | 86400000 | Expiração do token em ms (24h) |

> ⚠️ Em produção, sempre defina `JWT_SECRET` como variável de ambiente. Não suba credenciais no repositório.
