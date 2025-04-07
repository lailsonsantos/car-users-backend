# Car Users API

API RESTful para gerenciamento de usuários e carros. Desenvolvida com Spring Boot, documentada com Swagger e analisada com SonarQube. Artefatos são integrados via Jenkins CI/CD.

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
9. Eu, como desenvolvedor, desejo adicionaro plugin do Jacoco.

> A primeira linha de cada commit no repositório deve utilizar a descrição da estória de usuário associada.

---

## 🛠️ Solução Técnica

- **Spring Boot** com arquitetura REST
- **JWT (JSON Web Token)** para autenticação
- **Spring Security** para proteção de endpoints
- **Swagger (springdoc-openapi)** para documentação
- **SonarQube** para análise de código
- **Jenkins** com pipeline automatizado
- **Docker Compose** para orquestração local de Jenkins, SonarQube e PostgreSQL
- **Heroku** como plataforma de deploy final (sem necessidade de configurar servidor próprio)
- **Jacoco** como plugin para verificar a cobertura dos testes unitários

Justificamos o uso dessas tecnologias por sua robustez, padronização de mercado, fácil integração com ferramentas de CI/CD e alta compatibilidade com ambientes em nuvem como Heroku.

---

## ❗ Erros e Rotas da API

### 🔐 Autenticação e Usuários

| URL | Descrição | Códigos de erro |
|-----|-----------|-----------------|
| `/api/signin` | Espera `login` e `password`, retorna JWT com informações do usuário logado | 1 |
| `/api/users` | Cadastrar novo usuário | 2, 3, 4, 5 |
| `/api/users/{id}` | Atualizar usuário pelo ID | 2, 3, 4, 5 |

**Erros possíveis:**
- **1**: Login inexistente ou senha inválida → `"Invalid login or password"`
- **2**: E-mail já existente → `"Email already exists"`
- **3**: Login já existente → `"Login already exists"`
- **4**: Campos inválidos → `"Invalid fields"`
- **5**: Campos não preenchidos → `"Missing fields"`

---

### 👤 Perfil e Carros

| URL | Descrição | Códigos de erro |
|-----|-----------|-----------------|
| `/api/me` | Retorna dados do usuário logado, como nome, email, carros, `createdAt` e `lastLogin` | 1, 2 |
| `/api/cars` | Listar todos os carros do usuário logado | 1, 2 |
| `/api/cars` | Cadastrar novo carro | 1, 2, 3, 4, 5 |
| `/api/cars/{id}` | Buscar carro por ID | 1, 2 |
| `/api/cars/{id}` | Remover carro por ID | 1, 2 |
| `/api/cars/{id}` | Atualizar carro por ID | 1, 2, 3, 4, 5 |

**Erros possíveis:**
- **1**: Token não enviado → `"Unauthorized"`
- **2**: Token expirado → `"Unauthorized - invalid session"`
- **3**: Placa já existente → `"License plate already exists"`
- **4**: Campos inválidos → `"Invalid fields"`
- **5**: Campos não preenchidos → `"Missing fields"`

---

## 🔀 ROTAS DA API

### 🔐 Autenticação
- `POST /api/signin` → Login do usuário

### 👤 Usuários
- `POST /api/users` → Cadastro
- `PUT /api/users/{id}` → Atualização
- `GET /api/me` → Dados do usuário logado

### 🚗 Carros
- `GET /api/cars` → Listar carros
- `POST /api/cars` → Criar carro
- `GET /api/cars/{id}` → Buscar por ID
- `PUT /api/cars/{id}` → Atualizar
- `DELETE /api/cars/{id}` → Excluir

---

## 🚀 Como executar o projeto

### ✅ Pré-requisitos

- Java 17+
- Docker e Docker Compose
- Maven ou Wrapper (`./mvnw`)

### 🔧 Subindo Jenkins e SonarQube localmente

```bash
docker compose up -d
```

Acesse:
- Jenkins: http://localhost:8081
- SonarQube: http://localhost:9000 (login padrão: `admin` / `admin`)

### ▶️ Rodar a aplicação localmente

```bash
./mvnw spring-boot:run
```

Acesse Swagger em:
http://localhost:8080/swagger-ui.html

### 📆 Build e Testes

```bash
./mvnw clean install
./mvnw test
```

---

## ☁️ Deploy no Heroku (Manual)

### 🔗 URL de produção:

```
https://car-users-backend-3ffe4145b6c3.herokuapp.com
```

Esse endereço simula o mesmo comportamento de um backend local em `http://localhost:8080`

### 🔄 Passos para criar e fazer deploy manual para o Heroku

```bash
# Login no Heroku
heroku login

# Criar app (caso não exista)
heroku create nome-do-app

# Adicione manualmente o app
heroku git:remote -a nome-do-seu-app

# Fazer deploy
git push heroku main

# Ver logs
heroku logs --tail
```

Certifique-se de que o arquivo `Procfile` está presente com o conteúdo:
```
web: java -Dserver.port=$PORT -jar target/*.jar
```

---

## 📄 Jenkinsfile e SonarQube

### 🧹 Jenkinsfile

Contém:
- Etapa de **checkout**
- Etapa de **build e testes**
- Etapa de **análise SonarQube**
- Etapa de **Quality Gate**

> Certifique-se de configurar o Sonar em: `Gerenciar Jenkins > Configurar o Sistema > SonarQube servers`

### ⚙️ Como configurar e executar análise SonarQube

1. Gere token em: http://localhost:9000/account/security
2. Configure `sonar-project.properties`:

```properties
sonar.projectKey=car-users-backend
sonar.sources=src/main/java
sonar.tests=src/test/java
sonar.host.url=http://localhost:9000
sonar.login=SEU_TOKEN_DO_SONAR
```

3. Execute:
```bash
./mvnw sonar:sonar -Dsonar.login=SEU_TOKEN_DO_SONAR
```

> Substitua `SEU_TOKEN_DO_SONAR` pelo token real gerado.

---

## 📘 Swagger

Disponível em:
📌 `http://localhost:8080/swagger-ui.html`

---

## 👤 Autor

Lailson Santos - [github.com/lailsonsantos](https://github.com/lailsonsantos)
