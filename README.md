

# 🏋️ WischGym Backend ![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-brightgreen) ![MySQL](https://img.shields.io/badge/MySQL-Database-blue) ![JWT](https://img.shields.io/badge/Auth-JWT-yellow)

Essa é uma API REST para gerenciamento de academias, desenvolvida com **Java e Spring Boot**, contendo autenticação segura com JWT, controle de usuários por roles, gestão de alunos, planos, matrículas e módulo financeiro completo.

O sistema foi projetado com regras reais de negócio de academia, incluindo controle de vencimentos, status automático de matrículas e gestão de pagamentos.

---


# 📌 Visão Geral

O **WischGym Backend** fornece uma API segura e estruturada para gerenciamento de academias.

Principais responsabilidades da API:

* Autenticação de usuários
* Gestão de alunos
* Gestão de planos
* Controle de matrículas
* Controle financeiro
* Dashboard administrativo
* Segurança com JWT e Refresh Token

A API segue o padrão **RESTful** e foi construída seguindo boas práticas de arquitetura em aplicações Spring.

---

# ✨ Features

- Autenticação segura com JWT
- Refresh Token
- Controle de roles (ADMIN, PROFESSOR, ALUNO)
- Gestão completa de alunos
- Gestão de planos
- Sistema de matrículas
- Controle financeiro
- Atualização automática de pagamentos
- Bloqueio automático de alunos inadimplentes
- Dashboard administrativo
- Documentação automática com Swagger
---


## 🏗 Arquitetura da Aplicação

A arquitetura do sistema segue o padrão de camadas utilizado em aplicações Spring.

```text
Cliente (Frontend / Postman)
        │
        ▼
Controller Layer
        │
        ▼
Service Layer
(Regras de Negócio)
        │
        ▼
Repository Layer
(Spring Data JPA)
        │
        ▼
Banco de Dados (MySQL)
```

### Fluxo de requisição

1. O cliente realiza uma requisição HTTP.
2. O Controller recebe a requisição.
3. O Service executa as regras de negócio.
4. O Repository acessa o banco de dados.
5. A resposta é retornada ao cliente.

Essa separação garante:

* organização do código
* facilidade de manutenção
* escalabilidade da aplicação

---

# 🛠 Tecnologias Utilizadas

Backend desenvolvido utilizando:

* Java 17+
* Spring Boot
* Spring Security
* JWT (JSON Web Token)
* Spring Data JPA
* MySQL
* Maven
* Swagger / OpenAPI
* Lombok

---

# 🔐 Segurança da Aplicação

A aplicação utiliza **Spring Security com autenticação baseada em JWT**.

### Autenticação

O fluxo de autenticação funciona da seguinte forma:

1. Usuário envia email e senha para login
2. API valida credenciais
3. Um **Access Token** e um **Refresh Token** são gerados
4. O Access Token é utilizado para acessar endpoints protegidos

---

### Access Token

Token JWT que contém:

* email do usuário
* roles
* data de expiração

Ele deve ser enviado no header das requisições:

```
Authorization: Bearer TOKEN
```

---

### Refresh Token

Permite gerar um novo Access Token sem necessidade de novo login.

Fluxo:

```
Login → Access Token + Refresh Token
Access Token expira
Cliente usa Refresh Token
Novo Access Token é gerado
```

---

### Controle de Roles

O sistema possui três níveis de acesso:

* ADMIN
* PROFESSOR
* ALUNO

Exemplo de restrições:

* ADMIN possui acesso total
* PROFESSOR possui acesso limitado
* ALUNO possui acesso apenas a seus próprios dados

---

## 🧩 Modelo de Dados

O sistema possui as seguintes entidades principais:

```text
User
 ├── id
 ├── email
 ├── password
 └── roles

Aluno
 ├── id
 ├── nome
 ├── email
 ├── telefone
 └── status

Plano
 ├── id
 ├── nome
 ├── preco
 └── duracaoDias

Matricula
 ├── id
 ├── aluno_id
 ├── plano_id
 ├── dataInicio
 ├── dataVencimento
 └── status

Pagamento
 ├── id
 ├── matricula_id
 ├── valor
 ├── dataPagamento
 └── status
```

### Relacionamentos

```text
Aluno 1 ---- N Matricula
Plano 1 ---- N Matricula
Matricula 1 ---- N Pagamento
```

Regras importantes:

* Um aluno pode possuir várias matrículas ao longo do tempo
* Apenas **uma matrícula pode estar ativa**
* Pagamentos pertencem a uma matrícula


---

## Pagamento

Controla pagamentos realizados pelos alunos.

Campos principais:

* matrícula
* valor
* data de pagamento
* status

Status possíveis:

* PAGO
* PENDENTE
* ATRASADO

---

# ⚙️ Regras de Negócio Implementadas

O sistema possui regras que simulam o funcionamento real de uma academia.

### Matrícula

* um aluno só pode possuir **uma matrícula ativa**
* matrículas possuem data de vencimento
* matrículas podem ser renovadas

---

### Renovação de Matrícula

Ao renovar uma matrícula:

* nova data de vencimento é calculada
* matrícula permanece ativa

---

### Cancelamento

Uma matrícula pode ser cancelada, alterando seu status para **INATIVA**.

---

### Controle Financeiro

O sistema possui controle financeiro completo.

Funcionalidades:

* registrar pagamentos
* listar pagamentos por aluno
* listar histórico financeiro
* atualizar status automaticamente

---

### Atualização Automática de Pagamentos

Pagamentos vencidos são automaticamente atualizados para **ATRASADO**.

---

### Bloqueio Automático de Alunos

Caso um aluno possua pagamentos em atraso, ele pode ser automaticamente marcado como **INATIVO** no sistema.

---

# 📊 Dashboard Administrativo

O sistema possui endpoints de dashboard que fornecem métricas como:

* total de alunos
* alunos ativos
* alunos inadimplentes
* faturamento

Essas informações auxiliam no controle administrativo da academia.

---

# 📚 Documentação da API

A API possui documentação automática gerada com **Swagger**.

Após rodar a aplicação, acesse:

```
http://localhost:8080/swagger-ui.html
```

Ou:

```
http://localhost:8080/swagger-ui/index.html
```

---

# 🚀 Como Executar o Projeto

### 1️⃣ Clonar o repositório

```
git clone https://github.com/MuriloWisch/wischgym-backend.git
```

---

### 2️⃣ Configurar banco de dados

Criar um banco MySQL:

```
WischGym
```

Configurar no `application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/wischgym
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

---

### 3️⃣ Executar o projeto

Rodar a aplicação:

```
mvn spring-boot:run
```

ou executar a classe principal do Spring Boot.

---

## 📡 Exemplos de Uso da API

Abaixo estão alguns exemplos de como utilizar os endpoints.

---

### 🔑 Login

Endpoint:

```http
POST /auth/login
```

Request:

```json
{
  "email": "admin@admin.com",
  "password": "password"
}

Obs: usuario apenas para exemplo.
```

Response:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "ba338b1c-063c..."
}
```

---

### 👤 Criar Aluno

Endpoint:

```http
POST /alunos
```

Header:

```http
Authorization: Bearer TOKEN
```

Request:

```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "999999999"
}
```

Response:

```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "status": "ATIVO"
}
```

---

### 🏋️ Criar Plano

Endpoint:

```http
POST /planos
```

Request:

```json
{
  "nome": "Plano Mensal",
  "preco": 120.00,
  "duracaoDias": 30
}
```

---

### 📄 Criar Matrícula

Endpoint:

```http
POST /matriculas
```

Request:

```json
{
  "alunoId": 1,
  "planoId": 2
}
```

Response:

```json
{
  "id": 10,
  "dataInicio": "2026-03-16",
  "dataVencimento": "2026-04-16",
  "status": "ATIVA"
}
```

---

### 💳 Registrar Pagamento

Endpoint:

```http
POST /pagamentos
```

Request:

```json
{
  "matriculaId": 10,
  "valor": 120.00
}
```

Response:

```json
{
  "status": "PAGO",
  "dataPagamento": "2026-03-16"
}
```


---

# 🧪 Testes da API

A API foi testada utilizando:

* Postman
* Swagger UI

Todos os endpoints protegidos exigem **token JWT válido**.

---

# 🚧 Status do Projeto

Backend: ✔ Concluído  
Frontend: 🚧 Em desenvolvimento

# 👨‍💻 Autor

Projeto desenvolvido por **Murilo Wisch** como estudo avançado de **backend com Spring Boot**, focado em construção de APIs seguras e aplicação de regras de negócio reais.
