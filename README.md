# Pitang Challenge – Backend (Java Spring Boot)

Este projeto é a API backend para o desafio técnico, desenvolvida com Spring Boot.

## ⚙️ Tecnologias

- Java 17
- Spring Boot
- JPA + H2 Database (banco em memória)
- Maven
- Docker + Jenkins (CI/CD)
- Heroku (deploy)
- Postman (testes)

---

## ▶️ Como executar localmente

```bash
# Clonar o projeto
git clone https://github.com/seu-usuario/pitang-challenge-backend.git
cd pitang-challenge-backend

# Rodar com Maven
./mvnw spring-boot:run
# ou
mvn spring-boot:run

🧪 Testes
mvn test

🐳 Docker
docker build -t pitang-backend .
docker run -p 8080:8080 pitang-backend

⚙️ Deploy no Heroku
# Login
heroku login

# Criar app
heroku create pitang-app-backend

# Deploy
git push heroku main
heroku open

🤖 Jenkins (CI/CD)
Este projeto contém um Jenkinsfile com:

Build Maven

Deploy automático para o Heroku via Git

Requer configurações:

github-token (GitHub)

heroku-api-key (Heroku)

📫 Rotas principais
POST	/api/signin	Login
GET	/api/users	Listar usuários
POST	/api/users	Criar usuário
PUT	/api/users/{id}	Atualizar usuário
DELETE	/api/users/{id}	Remover usuário
GET	/api/cars	Listar carros
POST	/api/cars	Criar carro

🧪 Testes via Postman
Importe o arquivo pitang-challenge-api.postman_collection.json para testar as rotas.