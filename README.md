# Car Users API

API RESTful para gerenciamento de usuários e carros. Desenvolvida com Spring Boot, documentada com Swagger e analisada com SonarQube. Artefatos são integrados via Jenkins CI/CD.

---

## Funcionalidades

- **Autenticação e gerenciamento de usuários:** Cadastro, atualização, remoção e consulta de usuários.
- **Gerenciamento de carros:** Cada usuário pode cadastrar, atualizar, consultar e remover seus carros.
- **DTOs para Requests e Responses:** Os Controllers agora recebem _requests_ via DTO e retornam _responses_ via DTO.
- **Validações customizadas e exceções padronizadas:** Erros (como token não enviado, token expirado, campos inválidos ou ausentes, placa duplicada, etc.) são tratados via exceções customizadas com códigos e mensagens definidos.
- **Testes unitários 70% de cobertura:** Os métodos possuem testes unitários utilizando JUnit e Mockito.
- **Documentação via Javadoc:** Classes e métodos estão documentados com Javadoc.

---

## 📚 Estórias de Usuário (Scrum)

1. Eu, como usuário, desejo me autenticar com login e senha, recebendo um token JWT.
2. Eu, como usuário, desejo criar uma conta com meus dados pessoais e meus carros.
3. Eu, como usuário autenticado, desejo acessar meus dados, incluindo carros.
4. Eu, como usuário autenticado, desejo adicionar, editar e remover meus carros.
5. Eu, como administrador, desejo listar, buscar, atualizar e remover usuários.
6. Eu, como desenvolvedor, desejo visualizar a documentação via Swagger.
7. Eu, como desenvolvedor, desejo garantir qualidade de código via SonarQube.
8. Eu, como devops, desejo automatizar o build/teste/deploy via Jenkins.

---

## 🛠️ Solução Técnica

- **Spring Boot** com arquitetura REST
- **JWT (JSON Web Token)** para autenticação
- **Spring Security** para proteção de endpoints
- **Swagger (springdoc-openapi)** para documentação
- **SonarQube** para análise de código
- **Jenkins** com pipeline automatizado

---

## 🚀 Como executar o projeto

### Pré-requisitos:
- Java 17+
- Docker + Docker Compose
- Maven ou Wrapper (`./mvnw`)

### Comandos:

```bash
# Subir Jenkins e SonarQube
docker compose up -d

# Rodar localmente (em outra aba)
./mvnw spring-boot:run

# Acessar Swagger
http://localhost:8080/swagger-ui.html
```

---

## 📦 API

### 🔓 Rotas públicas (não requer token)

| Método | Rota             | Descrição                       |
|--------|------------------|---------------------------------|
| POST   | /api/signin      | Login com login/senha (JWT)     |
| GET    | /api/users       | Listar usuários                 |
| POST   | /api/users       | Criar usuário                   |
| GET    | /api/users/{id}  | Buscar usuário por ID           |
| PUT    | /api/users/{id}  | Atualizar usuário por ID        |
| DELETE | /api/users/{id}  | Remover usuário por ID          |

### 🔐 Rotas protegidas (JWT obrigatório)

| Método | Rota             | Descrição                       |
|--------|------------------|---------------------------------|
| GET    | /api/me          | Informações do usuário logado   |
| GET    | /api/cars        | Listar carros do usuário logado |
| POST   | /api/cars        | Criar carro                     |
| GET    | /api/cars/{id}   | Buscar carro por ID             |
| PUT    | /api/cars/{id}   | Atualizar carro por ID          |
| DELETE | /api/cars/{id}   | Remover carro por ID            |

---

## 🧪 Exemplo JSON de criação de usuário

```json
{
  "firstName": "Hello",
  "lastName": "World",
  "email": "hello@world.com",
  "birthday": "1990-05-01",
  "login": "hello.world",
  "password": "h3llo",
  "phone": "988888888",
  "cars": [
    {
      "year": 2018,
      "licensePlate": "PDV-0625",
      "model": "Audi",
      "color": "White"
    }
  ]
}
```

---

## ⚠️ Tratamento de Erros

- `Invalid login or password`
- `Login already exists`
- `Email already exists`
- `Invalid fields`
- `Missing fields`
- `Unauthorized`
- `License plate already exists`

---

## 🧪 Testes

```bash
./mvnw test
```

---

## 👷 Jenkins CI

Jenkinsfile pronto com:
- Build e testes
- Análise SonarQube

---

## 📘 Swagger

Disponível em:  
📎 `http://localhost:8080/swagger-ui.html`

---

## 👤 Autor

Lailson Santos - [github.com/lailsonsantos](https://github.com/lailsonsantos)
