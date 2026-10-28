# Restaurant API - Pós-Tech FIAP

API REST para gestão compartilhada de restaurantes: usuários, autenticação, restaurantes, funcionários, cardápio e pedidos. Desenvolvida com Spring Boot 4.1 e Java 25, organizada em Clean Architecture.

## Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Configuração](#configuração)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Tratamento de Erros](#tratamento-de-erros)
- [Testes e Cobertura](#testes-e-cobertura)
- [Documentação e Collection](#documentação-e-collection)
- [Troubleshooting](#troubleshooting)

---

## Visão Geral

- Cadastro e autenticação de usuários com JWT (senhas com BCrypt)
- CRUD de restaurantes com nome, CNPJ, endereço, tipo de cozinha e horário de funcionamento
- Quem cria o restaurante passa a ser o **dono**; o dono pode adicionar funcionários e promover outros donos
- Cadastro dos itens do cardápio (nome, descrição, preço, disponibilidade apenas no local e caminho da foto)
- Pedidos com fluxo de status (`IN_PROGRESS` → `DELIVERED` ou `CANCELED`)
- Erros padronizados no formato RFC 7807 (`ProblemDetail`)
- Documentação automática com OpenAPI/Swagger

---

## Tecnologias

| Categoria | Tecnologia |
|---|---|
| Linguagem | Java 25 |
| Framework | Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security) |
| Banco de dados | PostgreSQL 17 |
| Autenticação | JWT (jjwt 0.12) + BCrypt |
| Documentação | SpringDoc OpenAPI 3 (Swagger UI) |
| Testes | JUnit 5, Mockito, Testcontainers, JaCoCo |
| Build / Infra | Maven Wrapper, Docker, Docker Compose |

---

## Arquitetura

O projeto segue Clean Architecture: as dependências apontam sempre para dentro, e as regras de negócio não conhecem frameworks.

```
┌──────────────────────────────────────────────────────────────┐
│ presentation    Controllers · DTOs · Presenters · Exceptions │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ infrastructure  JPA · Adapters · Security · Config     │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │ application   Use cases · Commands · Gateways    │  │  │
│  │  │  ┌────────────────────────────────────────────┐  │  │  │
│  │  │  │ domain   Modelos · Regras · Ports          │  │  │  │
│  │  │  └────────────────────────────────────────────┘  │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

| Camada | Responsabilidade | Depende de |
|---|---|---|
| **domain** | Modelos em Java puro (`User`, `Restaurant`, `Item`, `Order`...), regras de negócio e interfaces de repositório (ports). Sem Spring, JPA, Jakarta ou Lombok. | nada |
| **application** | Um use case por operação (`CreateRestaurantUseCase`, `CancelOrderUseCase`...), commands de entrada e gateways (`PasswordEncoderGateway`, `TokenGateway`). Também sem frameworks. | domain |
| **infrastructure** | Entidades JPA, repositórios Spring Data, adapters que implementam os ports do domínio, segurança (JWT/BCrypt) e configuração. | application, domain |
| **presentation** | Controllers REST, DTOs de request/response, presenters (domínio → resposta HTTP) e tratamento global de erros. | application, domain |

**Decisões principais**

- **Domínio rico**: as regras ficam nos próprios modelos, por exemplo `order.cancel()` (valida o status), `restaurant.ensureOwner(user)` (autorização) e `item.disable()` (soft delete).
- **Entidades JPA separadas do domínio**: `UserEntity`, `RestaurantEntity` etc. ficam em `infrastructure/persistence/entity` e são convertidas pelo `PersistenceMapper`.
- **Use cases sem Spring**: são classes Java comuns marcadas com a anotação própria `@UseCase`; o `UseCaseConfig` (infraestrutura) as registra como beans.
- **Presenters**: os controllers não montam respostas; delegam ao presenter de cada recurso.
- **Restaurante como agregado**: os vínculos dono/funcionário fazem parte do `Restaurant` e são persistidos pelo `RestaurantRepositoryAdapter`.

**Fluxo de uma requisição**

```
HTTP → Controller → Request.toCommand() → UseCase.execute()
                                              ↓
                       Domain model (regras)  ←→  Repository port
                                                       ↓
                                       Adapter → Spring Data JPA → PostgreSQL
HTTP ← Response DTO ← Presenter ← domain model
```

---

## Estrutura do Projeto

```
src/main/java/br/com/fiap/restaurant/
├── domain/
│   ├── model/            # User, Restaurant, RestaurantMember, Item, Order, Address, PageResult
│   ├── enums/            # UserType, KitchenType, ItemType, OrderStatus
│   ├── exception/        # Exceções de negócio
│   └── repository/       # Ports de persistência (interfaces)
├── application/
│   ├── gateway/          # Ports de serviços externos (senha, token)
│   └── usecase/          # auth/, user/, restaurant/, item/, order/ + @UseCase
├── infrastructure/
│   ├── config/           # JPA, OpenAPI, registro dos use cases
│   ├── persistence/
│   │   ├── entity/       # Entidades JPA
│   │   ├── jpa/          # Repositórios Spring Data
│   │   ├── adapter/      # Implementações dos ports do domínio
│   │   ├── mapper/       # Conversão entidade JPA ↔ domínio
│   │   └── audit/        # Auditoria (createdBy, updatedAt...)
│   └── security/         # Spring Security, JWT, BCrypt
├── presentation/
│   ├── controller/       # Controllers REST
│   ├── dto/              # request/ e response/
│   ├── presenter/        # Domínio → DTO de resposta
│   ├── exception/        # Exception handler global (ProblemDetail)
│   ├── swagger/          # Exemplos de erro da documentação
│   └── common/           # Paths e constantes da API
└── RestaurantApplication.java

src/test/java/...         # Testes espelhando a estrutura acima
Collection-Insomnia.yaml  # Collection para testar a API
compose.yaml              # PostgreSQL + aplicação
Dockerfile                # Imagem da aplicação
```

---

## Pré-requisitos

- **Docker** e Docker Compose: suficientes para subir a aplicação (opção A)
- **Java 25**: necessário apenas para rodar localmente ou executar os testes (o build não compila com versões anteriores)
- Maven não é necessário: use o wrapper incluído (`./mvnw` ou `mvnw.cmd`)

---

## Configuração

O arquivo `.env` não é versionado. Crie-o na raiz do projeto:

```env
POSTGRES_DB=restaurant_db
POSTGRES_USER=restaurant_user
POSTGRES_PASSWORD=password
```

Variáveis lidas pela aplicação:

| Variável | Descrição | Padrão |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC do PostgreSQL | definida pelo `compose.yaml` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | definida pelo `compose.yaml` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | definida pelo `compose.yaml` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estratégia de schema do Hibernate | `update` |
| `SPRING_JPA_SHOW_SQL` | Exibe o SQL gerado | `true` |
| `JWT_SECRET` | Chave HMAC (Base64, mínimo 256 bits) | chave de desenvolvimento |
| `JWT_EXPIRATION_MS` | Validade do token em ms | `3600000` (1h) |

---

## Como Executar

### Opção A: tudo com Docker Compose

Basta o Docker; não é preciso ter Java nem Maven instalados. O `Dockerfile` é multi-stage: compila o projeto dentro de uma imagem com JDK 25 e gera uma imagem final enxuta, só com a JRE e o jar, executando com um usuário sem privilégios de root.

```bash
docker compose up -d --build
```

O primeiro build baixa as dependências e leva cerca de 1 minuto. Os seguintes reaproveitam o cache do Maven. Rode o mesmo comando depois de alterar o código para reconstruir a imagem.

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Debug remoto (JDWP): porta `5005`

### Opção B: aplicação local + banco no Docker

```bash
docker compose up -d postgres
```

Exporte as variáveis de conexão (ajuste conforme o seu `.env`) e rode a aplicação:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/restaurant_db SPRING_DATASOURCE_USERNAME=restaurant_user SPRING_DATASOURCE_PASSWORD=password SPRING_DOCKER_COMPOSE_ENABLED=false && ./mvnw spring-boot:run
```

---

## Endpoints da API

Base: `/api/v1`. Com exceção do cadastro de usuário e do login, todas as rotas exigem o header `Authorization: Bearer <token>`.

**Regras de autorização**
- **Dono**: quem criou o restaurante ou foi promovido. Pode editar o restaurante e gerenciar funcionários.
- **Membro** (dono ou funcionário): pode gerenciar itens e pedidos e ver a equipe.
- **Qualquer usuário autenticado**: pode consultar restaurantes e fazer pedidos.

### Autenticação

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/auth/login` | Autentica e retorna o token JWT | público |

```json
// POST /api/v1/auth/login
{ "login": "joao_silva", "password": "senha1234" }

// 200 OK
{ "token": "eyJhbGciOiJIUzI1NiJ9...", "tokenType": "Bearer", "expiresInMs": 3600000 }
```

### Usuários

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/users` | Cadastra um usuário | público |
| GET | `/users` | Lista usuários | autenticado |
| GET | `/users/{userId}` | Busca usuário por id | autenticado |
| GET | `/users/name/{name}?page=0&size=10` | Busca paginada por nome (exato, sem diferenciar maiúsculas) | autenticado |
| PATCH | `/users/me` | Atualiza e-mail, login, telefone e/ou endereço do próprio usuário | autenticado |
| PATCH | `/users/me/password` | Altera a própria senha | autenticado |
| DELETE | `/users/me` | Desativa a própria conta (soft delete) | autenticado |

```json
// POST /api/v1/users
{
  "name": "João Silva",
  "email": "joao@email.com",
  "login": "joao_silva",
  "password": "senha1234",
  "phone": "11999998888",
  "address": { "street": "Rua A", "number": "123", "city": "São Paulo", "state": "SP", "zipCode": "01234-567" }
}
```

A busca por nome retorna `{ "content": [...], "page": 0, "size": 10, "totalElements": 1, "totalPages": 1 }`.

### Restaurantes

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| GET | `/restaurant` | Lista restaurantes com seus itens | autenticado |
| GET | `/restaurant/{restaurantId}` | Busca restaurante por id | autenticado |
| POST | `/restaurant` | Cria restaurante (o usuário autenticado vira dono) | autenticado |
| PATCH | `/restaurant/{restaurantId}` | Atualiza dados do restaurante | dono |
| GET | `/restaurant/{restaurantId}/employee` | Lista dono(s) e funcionários | membro |
| POST | `/restaurant/{restaurantId}/employee/{userId}` | Adiciona funcionário | dono |
| DELETE | `/restaurant/{restaurantId}/employee/{userId}` | Remove funcionário | dono |
| PATCH | `/restaurant/{restaurantId}/owner/{userId}` | Promove um membro a dono | dono |
| DELETE | `/restaurant/{restaurantId}` | Desativa o restaurante (soft delete) | dono |

```json
// POST /api/v1/restaurant
{
  "name": "Cantina da Nona",
  "cnpj": "12345678000199",
  "description": "Massas artesanais",
  "phone": "11988887777",
  "kitchenType": "ITALIAN_FOOD",
  "openingAt": "11:00:00",
  "closingAt": "23:00:00",
  "address": { "street": "Rua B", "number": "45", "city": "São Paulo", "state": "SP", "zipCode": "01000-000" }
}

// 201 Created
{
  "id": 1,
  "name": "Cantina da Nona",
  "cnpj": "12345678000199",
  "description": "Massas artesanais",
  "phone": "11988887777",
  "kitchenType": "ITALIAN_FOOD",
  "openingAt": "11:00:00",
  "closingAt": "23:00:00",
  "address": { "street": "Rua B", "number": "45", "neighborhood": null, "city": "São Paulo", "state": "SP", "zipCode": "01000-000", "complement": null },
  "owners": [ { "id": 1, "name": "João Silva", "login": "joao_silva" } ],
  "items": []
}
```

Na criação são obrigatórios `name`, `cnpj`, `kitchenType`, `openingAt`, `closingAt` e `address` (com `street`, `number`, `city`, `state` e `zipCode`). No `PATCH`, só os campos enviados são alterados, inclusive dentro de `address`. `owners` lista o dono e quem foi promovido; `items` traz apenas os itens ativos.

Tipos de cozinha: `ITALIAN_FOOD`, `BRAZILIAN_FOOD`, `JAPANESE_FOOD`, `MEXICAN_FOOD`, `CHINESE_FOOD`, `ARABIC_FOOD`, `FAST_FOOD`, `VEGAN_FOOD`, `OTHER`.

### Itens do cardápio

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| GET | `/restaurant/{restaurantId}/item` | Lista o cardápio (itens ativos) | autenticado |
| GET | `/restaurant/{restaurantId}/item/{itemId}` | Busca um item | autenticado |
| POST | `/restaurant/{restaurantId}/item` | Cria item | membro |
| PATCH | `/restaurant/{restaurantId}/item/{itemId}` | Atualiza item | membro |
| DELETE | `/restaurant/{restaurantId}/item/{itemId}` | Desativa item (soft delete) | membro |

```json
// POST /api/v1/restaurant/1/item
{
  "name": "Lasanha",
  "description": "Lasanha à bolonhesa",
  "value": 59.90,
  "imageUrl": "/images/lasanha.png",
  "itemType": "JUST_IN_PERSON"
}
```

`itemType` define a disponibilidade: `JUST_IN_PERSON` (apenas no restaurante), `JUST_DELIVERY` ou `DELIVERY_OR_IN_PERSON` (padrão). `imageUrl` guarda apenas o caminho da foto.

### Pedidos

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/restaurant/{restaurantId}/order` | Cria pedido com itens do restaurante | autenticado |
| GET | `/restaurant/{restaurantId}/order` | Lista pedidos do restaurante | membro |
| PATCH | `/restaurant/{restaurantId}/order/{orderId}/deliver` | Marca como entregue | membro |
| PATCH | `/restaurant/{restaurantId}/order/{orderId}/cancel` | Cancela | membro |

```json
// POST /api/v1/restaurant/1/order
{ "itemIds": [1, 2] }
```

Só pedidos `IN_PROGRESS` podem ser entregues ou cancelados.

### Exclusão lógica (soft delete)

Usuários, restaurantes e itens não são apagados do banco: a exclusão marca o registro como inativo (`enabled = false`). A partir daí:

- **Item deletado**: some do cardápio e do restaurante, retorna 404 e não pode ser editado nem incluído em pedidos. Pedidos antigos continuam mostrando o item.
- **Restaurante deletado**: some da listagem, e ele e seus itens, pedidos e funcionários retornam 404.
- **Usuário desativado**: não consegue fazer login, o token emitido antes deixa de valer (401), e ele some das listagens e buscas.

---

## Tratamento de Erros

Todas as respostas de erro seguem o formato `ProblemDetail` (RFC 7807):

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/v1/users",
  "validationErrors": { "email": "Email must be valid" }
}
```

| Status | Quando ocorre |
|---|---|
| 400 | Payload inválido (Bean Validation) ou regra de domínio violada (ex.: preço ≤ 0) |
| 401 | Token ausente/inválido ou login/senha incorretos |
| 403 | Usuário não é dono/membro do restaurante |
| 404 | Recurso não encontrado |
| 409 | Dado único duplicado (e-mail, login, CNPJ...), funcionário já vinculado ou pedido fora de `IN_PROGRESS` |

---

## Testes e Cobertura

```bash
./mvnw clean verify
```

**Testes unitários**: domínio, use cases (repositórios mockados), adapters de persistência, segurança, presenters e controllers.

**Testes de integração** (`src/test/java/.../integration`): sobem a aplicação completa e chamam a API via MockMvc, passando pelo filtro JWT, validação, use cases, JPA e um PostgreSQL 17 real criado pelo Testcontainers.

| Classe | O que cobre |
|---|---|
| `AuthIntegrationTest` | Cadastro com senha em BCrypt, login, credenciais inválidas, duplicidade, rotas protegidas, token revogado ao desativar a conta |
| `UserIntegrationTest` | Consulta, listagem, atualização parcial com merge de endereço, troca de senha, busca paginada por nome, soft delete |
| `RestaurantIntegrationTest` | Criação com dono e auditoria, validações e CNPJ único, atualização só pelo dono, funcionários e promoção a dono, exclusão |
| `ItemIntegrationTest` | Cadastro com todos os campos do cardápio, validações, permissões, consulta, isolamento entre restaurantes, soft delete |
| `OrderIntegrationTest` | Criação de pedido, itens inválidos/deletados/de outro restaurante, permissões, ciclo de status (entregue/cancelado) |

O container e o contexto Spring são compartilhados por todas as classes, e as tabelas são limpas após cada teste. Os testes de integração precisam do Docker em execução; sem ele, são ignorados.

**Cobertura**: o JaCoCo exige no mínimo **80% de linhas** e falha o build abaixo disso. Relatório em `target/site/jacoco/index.html`.

---

## Documentação e Collection

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI (JSON)**: http://localhost:8080/v3/api-docs
- **Collections**, na raiz, com as mesmas requisições:
  - `Collection-Postman.json`: Postman (*Import → File*). Usa variáveis da própria collection, então não é preciso selecionar environment.
  - `Collection-Insomnia.yaml`: Insomnia (*Import → From File*). Usa o *Base Environment*.

As variáveis são `baseUrl`, `token`, `userId`, `employeeId`, `restaurantId`, `itemId` e `orderId`. Scripts executados após a resposta as preenchem automaticamente, e nenhum token fica salvo nos arquivos. Num banco limpo, execute nesta ordem:

1. **User**: `Create Owner User` → `Create Employee User` → `Login` (salva o `token`)
2. **Restaurant**: `Create Restaurant` (salva o `restaurantId`) → `Add Employee to Restaurant`
3. **Item**: `Create Item` (salva o `itemId`)
4. **Order**: `Create Order` (salva o `orderId`) → `Deliver Order` ou `Cancel Order`

O token expira em 1 hora; depois disso, execute `Login` novamente.

---

## Troubleshooting

**`release version 25 not supported`**: o Maven está usando um JDK antigo. Aponte o `JAVA_HOME` para um JDK 25.

**`Cannot load driver class: org.h2.Driver` nos testes**: há arquivos antigos em `target/`. Rode `./mvnw clean verify` em vez de apenas `verify`.

**`Unable to establish loopback connection` ao iniciar no Windows**: o JDK não conseguiu criar o socket interno na pasta temporária. Informe outra pasta:

```bash
java -Djdk.net.unixdomain.tmpdir=C:/temp -jar target/restaurant-0.0.1-SNAPSHOT.jar
```

**`Connection refused` (PostgreSQL)**: confira se o container está de pé com `docker compose ps`.

---

## Sobre

Projeto desenvolvido para o Tech Challenge (Fase 2) da Pós-Tech FIAP em Arquitetura e Desenvolvimento Java.

Versão: 0.0.1-SNAPSHOT
