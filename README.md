# Restaurant API - Pós-Tech FIAP

Uma API RESTful para gerenciamento de usuários, autenticação e operações de restaurante, desenvolvida com Spring Boot 4.1.0 e Java 25.

## Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Como Executar](#como-executar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Endpoints da API](#endpoints-da-api)
- [Documentação](#documentação)
- [Contribuindo](#contribuindo)

---

## Visão Geral

Este projeto é uma API REST robusta para gerenciamento de um restaurante, com as seguintes funcionalidades:

- Autenticação e Autorização com JWT e Spring Security
- Gerenciamento de Usuários com CRUD e validações
- Segurança com criptografia de senhas e tokens JWT
- Tratamento global de erros com Exception Handler
- Validação de dados com Jakarta Validation
- Documentação automática com OpenAPI/Swagger

---

## Tecnologias

Backend
- Framework: Spring Boot 4.1.0
- Linguagem: Java 25
- Build: Maven
- Banco de Dados: PostgreSQL
- ORM: Spring Data JPA (Hibernate)

Segurança
- Spring Security - Autenticação e autorização
- JWT - Json Web Tokens
- Lombok - Redução de boilerplate

Documentação
- SpringDoc OpenAPI - Documentação automática (Swagger/OpenAPI)

---

## Pré-requisitos

- Java 25 ou superior - Download em https://www.oracle.com/java/technologies/
- Maven 3.8 ou superior - Download em https://maven.apache.org/download.cgi
- PostgreSQL 12 ou superior - Download em https://www.postgresql.org/download/
- Docker (opcional) - Para utilizar Docker Compose

---

## Instalação

1. Clonar o repositório
```bash
git clone <repository-url>
cd restaurant
```

2. Instalar dependências
```bash
./mvnw clean install
```

No Windows:
```cmd
mvnw.cmd clean install
```

3. Configurar Banco de Dados

Opção A: PostgreSQL local
```sql
CREATE DATABASE restaurant_db;
CREATE USER restaurant_user WITH PASSWORD 'password';
ALTER ROLE restaurant_user SET client_encoding TO 'utf8';
GRANT ALL PRIVILEGES ON DATABASE restaurant_db TO restaurant_user;
```

Opção B: Docker Compose
```bash
docker-compose up -d
```

O arquivo compose.yaml está configurado para subir PostgreSQL automaticamente.

4. Configurar variáveis de ambiente

Crie um arquivo .env na raiz do projeto:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/restaurant_db
SPRING_DATASOURCE_USERNAME=restaurant_user
SPRING_DATASOURCE_PASSWORD=password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
JWT_SECRET=your-secret-key-here-min-256-bits
JWT_EXPIRATION=3600000
```

Ou configure em src/main/resources/application.yml:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/restaurant_db
    username: restaurant_user
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

jwt:
  secret: your-secret-key-here
  expiration: 3600000
```

---

## Como Executar

Executar a aplicação
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080

Acessar Swagger/OpenAPI
```
http://localhost:8080/swagger-ui.html
```

---

## Estrutura do Projeto

```
restaurant/
├── src/
│   ├── main/
│   │   ├── java/br/com/fiap/restaurant/
│   │   │   ├── controller/          # Controllers REST
│   │   │   ├── service/             # Serviços de negócio
│   │   │   ├── repository/          # Interfaces de acesso a dados
│   │   │   ├── entity/              # Entidades JPA
│   │   │   ├── dto/                 # DTOs (Request/Response)
│   │   │   ├── mapper/              # Mapeadores DTO ↔ Entity
│   │   │   ├── security/            # Configuração de segurança
│   │   │   ├── exception/           # Exceções customizadas
│   │   │   └── RestaurantApplication.java  # Classe principal
│   │   └── resources/
│   │       ├── application.yml      # Configurações
│   │       └── application-test.yml # Configurações de teste
│   └── test/
│       └── java/br/com/fiap/restaurant/
├── compose.yaml                     # Docker Compose
├── pom.xml                          # Dependências Maven
└── README.md                        # Este arquivo
```

---

## Endpoints da API

Autenticação

Login
```
POST /api/auth/login
Content-Type: application/json

{
  "login": "usuario@email.com",
  "password": "senha123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": { ... }
}
```

Registrar Usuário
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "login": "joao_silva",
  "password": "senha123",
  "address": {
    "street": "Rua A",
    "number": "123",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01234-567"
  }
}

Response: 201 Created
```

Usuários

Listar Usuários
```
GET /api/users
Headers: Authorization: Bearer <token>

Response: 200 OK
{
  "content": [ ... ],
  "totalElements": 10,
  "totalPages": 1
}
```

Obter Usuário por ID
```
GET /api/users/{id}
Headers: Authorization: Bearer <token>

Response: 200 OK
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@email.com",
  "login": "joao_silva",
  ...
}
```

Atualizar Usuário
```
PUT /api/users/{id}
Headers: Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "João Santos",
  "email": "joao.santos@email.com",
  ...
}

Response: 200 OK
```

Deletar Usuário
```
DELETE /api/users/{id}
Headers: Authorization: Bearer <token>

Response: 204 No Content
```

---

## Documentação

Swagger/OpenAPI
Após iniciar a aplicação, acesse:
```
http://localhost:8080/swagger-ui.html
```

Postman
Uma coleção do Postman está disponível em:
```
insomnia-export-[hash].json
```

Importe no Postman/Insomnia para testar todos os endpoints.

---

## Segurança

Autenticação
- Senhas são criptografadas com BCrypt
- Tokens JWT com expiração configurável
- Validação de tokens em cada requisição

Autorização
- Role-based access control (RBAC)
- Usuários só podem acessar/modificar seus próprios dados
- Endpoints protegidos requerem token válido

Validação
- Validação de entrada com Jakarta Validation
- Email único por usuário
- Login único por usuário
- Validação de formato de dados

---

## Troubleshooting

Erro: "Connection refused" (PostgreSQL)
```bash
# Verificar se PostgreSQL está rodando
docker-compose up -d
```

Erro: "Maven not found"
Use o Maven Wrapper incluído:
```bash
./mvnw --version
```

Erro: "Cannot compile"
Limpe e recompile:
```bash
./mvnw clean compile
```

---

---

## Desenvolvimento

Code Style
- Seguir convenções Java
- Usar Lombok para reduzir boilerplate
- Comentar apenas código complexo

Commits
```bash
git commit -m "feat: adicionar novo endpoint de usuário"
```

Pull Requests
1. Create uma branch: git checkout -b feature/minha-feature
2. Commit suas mudanças: git commit -m 'feat: descrição'
3. Push para a branch: git push origin feature/minha-feature
4. Abra um Pull Request

---

## Contribuindo

Contribuições são bem-vindas! Por favor:

1. Fork o projeto
2. Crie uma branch para sua feature (git checkout -b feature/AmazingFeature)
3. Commit suas mudanças (git commit -m 'Add some AmazingFeature')
4. Push para a branch (git push origin feature/AmazingFeature)
5. Abra um Pull Request

---

## Licença

Este projeto está licenciado sob a Licença MIT - consulte o arquivo LICENSE para detalhes.

---

## Suporte

Para suporte, abra uma issue no repositório ou entre em contato com a equipe de desenvolvimento.

---

## Sobre

Projeto desenvolvido como parte do programa de Pós-Graduação em Engenharia de Software da FIAP (Faculdade de Informática e Administração Paulista).

Versão: 0.0.1-SNAPSHOT
Última atualização: 2026
