# Desafio Full Stack — Benefícios e Transferência

Aplicação full stack para **consulta de benefícios** e **transferência de saldo entre benefícios**, com foco em **consistência transacional** e **concorrência** (correção do bug de "lost update").

## Funcionalidades
- Listar benefícios e visualizar saldo/status
- Consultar benefício por ID
- Transferir valor entre dois benefícios (origem → destino)
- Interface web (Angular) para executar a transferência
- Documentação via Swagger/OpenAPI

## Tecnologias

### Backend
- Java 17
- Maven (multi-módulo)
- Spring Boot
- JPA/Hibernate
- H2 Database (em memória)
- Swagger/OpenAPI (Springdoc)

### Frontend
- Angular 17 (standalone components)
- TypeScript
- HttpClient

## Estrutura do repositório

```
desafio/
├── backend-module/     # API REST + persistência + regras de negócio
├── ejb-module/         # Camada EJB (referência arquitetural para produção)
├── frontend/           # Aplicação Angular (UI)
└── pom.xml             # Parent POM (multi-módulo)
```

## Como rodar o projeto

### Pré-requisitos
- Java 17 (`java -version`)
- Maven (`mvn -v`)
- Node 18+ (`node -v`)
- Angular CLI (`ng version`)

> **Dica:** se usar `nvm`, garanta que o Node correto está ativo antes de rodar o frontend.

## Backend (Spring Boot)

### Rodar o backend
A partir da raiz do repositório:

```bash
mvn clean install
mvn spring-boot:run -pl backend-module
```

Ou rodando diretamente no módulo:

```bash
cd backend-module
mvn spring-boot:run
```

O backend estará disponível em: **http://localhost:8080**

## Banco de Dados (H2 – ambiente de desenvolvimento)

A aplicação utiliza **H2 em memória** para facilitar execução e avaliação do desafio.

### Configuração (`application.properties`)
```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true

spring.jpa.show-sql=true

spring.jpa.hibernate.ddl-auto=none

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.operationsSorter=method
```

### Inicialização de dados
- `schema.sql` → cria as tabelas
- `data.sql` → popula dados iniciais

Esses scripts são executados automaticamente na inicialização da aplicação.

### Console do H2
Após subir o backend, o console pode ser acessado em:

- `http://localhost:8080/h2-console`

**Parâmetros de login:**
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: *(em branco)*

## Swagger / OpenAPI

A API é documentada via Swagger (Springdoc):

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Endpoints principais
- `GET /api/beneficios` — lista benefícios
- `GET /api/beneficios/{id}` — busca por ID
- `POST /api/beneficios/transferir?fromId=1&toId=2&amount=10.00` — executa transferência

> **Observação:** o endpoint de transferência usa `@RequestParam` (query params) e retorna `text/plain`.

## Frontend (Angular)

### Instalar dependências
Dentro da pasta `frontend/`:

```bash
cd frontend
npm install
```

### Rodar o frontend
```bash
ng serve
```

O frontend estará disponível em: **http://localhost:4200**

### Como usar a UI
1. A tela lista os benefícios cadastrados
2. Cada linha tem um botão **"Transferir"** (define a origem)
3. Escolha o destino + valor e confirme
4. Após sucesso, a listagem é recarregada automaticamente para refletir os novos saldos

## Testes

### Rodar testes do backend
```bash
mvn test -pl backend-module
```

## Decisões técnicas

### 1) DTOs na API
A API retorna DTOs ao invés das entidades JPA:
- Menor acoplamento
- Evita exposição de detalhes internos
- Contrato de API mais estável

### 2) Concorrência e consistência
- Uso de **Optimistic Locking (`@Version`)**
- Validações de negócio (saldo, valor positivo, origem ≠ destino)
- Transação atômica (debita e credita ou rollback)

### 3) Sobre o `ejb-module`

O projeto inclui um módulo `ejb-module` representando uma **camada EJB legada**, comum em ambientes corporativos.

#### Decisão adotada no desafio
- O módulo **não é utilizado no runtime local**
- O backend roda de forma **standalone**
- **Não é necessário WildFly/JBoss** para executar o desafio

#### Justificativa
O `ejb-module` foi mantido apenas como **referência arquitetural** e **preparação para produção**.

✅ Em produção, ele poderia ser implantado em servidor Java EE e integrado (ex.: JNDI/REST/mensageria).

## Troubleshooting

### Erro Angular: build-angular não encontrado
Na pasta `frontend/`:

```bash
rm -rf node_modules package-lock.json
npm install
ng serve
```

## Portas padrão
- Backend: `8080`
- Frontend: `4200`

## Autor
Willian Sousa Farias.