# Sistema de Controle Administrativo - API Backend

API REST desenvolvida em Spring Boot para gestão administrativa de uma organização, incluindo controle de membros, gestão financeira, assistência social e sistema de permissões de usuários com autenticação baseada em JWT.

## Visão Geral

O Sistema de Controle Administrativo é uma API RESTful que fornece endpoints seguros para gerenciar diversos aspectos administrativos de uma organização. A aplicação utiliza autenticação JWT (JSON Web Token) para garantir segurança e controle de acesso baseado em roles e permissões granulares.

## Tecnologias Utilizadas

### Framework e Linguagem
- **Java 25** - Linguagem de programação
- **Spring Boot 4.0.1** - Framework para desenvolvimento de aplicações Java
- **Spring Security** - Framework de segurança e autenticação
- **Spring Data JPA** - Abstração para acesso a dados
- **Spring Web MVC** - Framework para construção de APIs REST

### Banco de Dados
- **H2 Database** - Banco de dados em memória (desenvolvimento)
- **JPA/Hibernate** - ORM para mapeamento objeto-relacional

### Segurança e Autenticação
- **JWT (JSON Web Token)** - Autenticação stateless via tokens
- **BCrypt** - Criptografia de senhas
- **Spring Security OAuth2 Client** - Suporte a OAuth2

### Documentação e Monitoramento
- **SpringDoc OpenAPI 3** - Documentação interativa da API (Swagger UI)
- **Spring Boot Actuator** - Monitoramento e métricas da aplicação
- **Prometheus** - Coleta de métricas

### Integração Externa
- **Spring Cloud OpenFeign** - Cliente HTTP declarativo para integração com APIs externas
- **ViaCEP API** - Integração para busca de endereços por CEP

### Ferramentas de Build
- **Gradle** - Gerenciador de dependências e build
- **Lombok** - Redução de boilerplate code

### Validação
- **Bean Validation (Jakarta)** - Validação de dados de entrada

## Pré-requisitos

Antes de executar a aplicação, certifique-se de ter instalado:

- **Java Development Kit (JDK) 25** ou superior
- **Gradle 8.x** ou superior (ou use o Gradle Wrapper incluído no projeto)
- **IDE** (IntelliJ IDEA, Eclipse, VS Code) - opcional, mas recomendado

## Instalação e Configuração

### 1. Clonar o Repositório

```bash
git clone <url-do-repositorio>
cd administrativo
```

### 2. Configurar Variáveis de Ambiente

⚠️ **IMPORTANTE:** A partir desta versão, a aplicação requer variáveis de ambiente para funcionar corretamente.

**Variáveis Obrigatórias:**

```bash
# JWT Secret - OBRIGATÓRIO para produção
# Gere uma chave segura com: openssl rand -base64 32
export JWT_SECRET=sua-chave-256-bits-aqui

# CORS - URLs permitidas (separadas por vírgula)
export ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
```

**Variáveis Opcionais:**

```bash
# Rate Limiting (valores padrão já configurados)
export RATE_LIMIT_GENERAL=100  # Requisições por minuto (geral)
export RATE_LIMIT_AUTH=5       # Requisições por minuto (login)

# JWT Expiração
export JWT_EXPIRATION=86400000  # 24 horas em milissegundos
```

📄 **Consulte o arquivo `.env.example` para mais detalhes e exemplos de configuração.**

### 3. Compilar o Projeto

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

## Executando a Aplicação

### Modo de Desenvolvimento

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

A aplicação estará disponível em `http://localhost:8080`

### Executar JAR

Após o build, você pode executar o JAR gerado:

```bash
java -jar build/libs/administrativo-0.0.1-SNAPSHOT.war
```

### Acessar Console H2

Durante o desenvolvimento, o console H2 está disponível em:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:administrativo`
- Username: `sa`
- Password: (deixe em branco)

## Autenticação e Geração de Token

### Fluxo de Autenticação

A aplicação utiliza autenticação baseada em JWT (JSON Web Token) com as seguintes características:

1. **Login**: O usuário faz login com email e senha
2. **Geração de Token**: O servidor valida as credenciais e gera um token JWT
3. **Uso do Token**: O token deve ser enviado no header `Authorization` de todas as requisições protegidas
4. **Validação**: O servidor valida o token em cada requisição através do filtro JWT

### Endpoint de Login

**POST** `/api/auth/login`

**Request Body:**
```json
{
  "email": "usuario@exemplo.com",
  "senha": "senha123"
}
```

**Response (200 OK):**
```json
{
  "message": "Login realizado com sucesso!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tipo": "Bearer",
    "usuarioId": 1,
    "nome": "João Silva",
    "email": "usuario@exemplo.com",
    "role": "USER"
  }
}
```

### Endpoint de Cadastro

**POST** `/api/auth/cadastro`

**Request Body:**
```json
{
  "nome": "João Silva",
  "email": "joao@exemplo.com",
  "senha": "senha123"
}
```

**Response (201 Created):**
```json
{
  "message": "Usuário cadastrado com sucesso!",
  "data": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@exemplo.com",
    "role": "USER",
    "dataCriacao": "2026-01-28T10:30:00"
  }
}
```

### Características do Token JWT

- **Tipo**: Bearer Token
- **Validade**: 24 horas (86400000 milissegundos) - configurável via `jwt.expiration`
- **Algoritmo**: HMAC SHA-256
- **Formato**: `Bearer <token>`

### Estrutura do Token

O token JWT contém:
- **Subject (sub)**: Email do usuário
- **Issued At (iat)**: Data de emissão
- **Expiration (exp)**: Data de expiração

## Como Usar as APIs via Postman

### 1. Configurar Ambiente no Postman

Crie um novo ambiente no Postman com as seguintes variáveis:

- `base_url`: `http://localhost:8080`
- `token`: (será preenchido após login)

### 2. Realizar Login

1. Crie uma nova requisição **POST**
2. URL: `{{base_url}}/api/auth/login`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "email": "admin@exemplo.com",
  "senha": "senha123"
}
```
5. Envie a requisição
6. Copie o token do campo `data.token` da resposta
7. Cole o token na variável `token` do ambiente

### 3. Configurar Autenticação Automática

Para facilitar o uso, configure um script de pré-requisição ou use a autenticação Bearer Token:

1. Vá em **Authorization** da requisição
2. Selecione **Bearer Token**
3. Cole o token ou use `{{token}}`

### 4. Criar Collection com Autenticação Global

1. Crie uma nova Collection
2. Vá em **Variables** da collection
3. Adicione a variável `token`
4. Vá em **Authorization**
5. Selecione **Bearer Token** e use `{{token}}`
6. Todas as requisições da collection herdarão esta configuração

### 5. Exemplos de Requisições

#### Listar Membros

**GET** `{{base_url}}/api/membros`

**Headers:**
```
Authorization: Bearer {{token}}
```

#### Cadastrar Membro

**POST** `{{base_url}}/api/membros`

**Headers:**
```
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Body:**
```json
{
  "nome": "Maria Santos",
  "cpf": "12345678900",
  "rg": "1234567",
  "ri": "RI001",
  "cargo": "Membro",
  "endereco": {
    "rua": "Rua Exemplo",
    "numero": "123",
    "cep": "12345678",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "complemento": "Apto 45"
  }
}
```

#### Buscar CEP

**GET** `{{base_url}}/api/cep/12345678`

**Headers:**
```
Authorization: Bearer {{token}}
```

## Arquitetura do Projeto

A aplicação segue uma arquitetura em camadas (Layered Architecture) com separação clara de responsabilidades:

```
src/main/java/com/adbrassacoma/administrativo/
├── domain/                          # Camada de Domínio
│   ├── enums/                      # Enumerações do domínio
│   │   ├── Role.java              # Roles: ADMIN, USER
│   │   └── TipoFinanceiro.java    # Tipos: DIZIMO, DESPESAS, REFORMAS, OFERTAS
│   ├── model/                     # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Membros.java
│   │   ├── Financeiro.java
│   │   ├── AssistenciaSocial.java
│   │   ├── Endereco.java
│   │   ├── PermissaoUsuario.java
│   │   └── TelaPermissao.java
│   └── service/                   # Lógica de negócio
│       ├── AuthService.java
│       ├── MembroService.java
│       ├── FinanceiroService.java
│       ├── AssistenciaSocialService.java
│       ├── PermissaoService.java
│       ├── CepService.java
│       └── TelaPermissaoDiscoveryService.java
│
├── infrastructure/                 # Camada de Infraestrutura
│   ├── config/                    # Configurações
│   │   ├── SecurityConfig.java    # Configuração de segurança
│   │   ├── JwtService.java        # Serviço de JWT
│   │   ├── JwtAuthenticationFilter.java  # Filtro de autenticação
│   │   ├── OpenApiConfig.java     # Configuração Swagger
│   │   ├── FeignConfig.java       # Configuração Feign
│   │   └── DataInitializer.java   # Inicialização de dados
│   │
│   ├── controller/                # Controllers REST
│   │   ├── AuthController.java
│   │   ├── MembroController.java
│   │   ├── FinanceiroController.java
│   │   ├── AssistenciaSocialController.java
│   │   ├── PermissaoController.java
│   │   └── CepController.java
│   │
│   ├── dto/                       # Data Transfer Objects
│   │   ├── request/               # DTOs de requisição
│   │   └── response/              # DTOs de resposta
│   │
│   ├── repository/                # Repositórios JPA
│   │   ├── UsuarioRepository.java
│   │   ├── MembrosRepository.java
│   │   ├── FinanceiroRepository.java
│   │   ├── AssistenciaSocialRepository.java
│   │   ├── EnderecoRepository.java
│   │   ├── PermissaoUsuarioRepository.java
│   │   └── TelaPermissaoRepository.java
│   │
│   ├── exception/                 # Tratamento de exceções
│   │   ├── GlobalExceptionHandler.java
│   │   ├── CustomAccessDeniedHandler.java
│   │   └── [Exceções customizadas]
│   │
│   ├── validator/                 # Validadores customizados
│   │   └── CpfValidator.java
│   │
│   └── client/                    # Clientes HTTP externos
│       ├── CorreiosClient.java
│       └── dto/
│           └── CorreiosCepResponse.java
│
└── AdministrativoApplication.java  # Classe principal
```

### Princípios de Arquitetura

1. **Separação de Responsabilidades**: Cada camada tem uma responsabilidade específica
2. **Inversão de Dependências**: A camada de domínio não depende da infraestrutura
3. **DTO Pattern**: Uso de DTOs para transferência de dados entre camadas
4. **Repository Pattern**: Abstração do acesso a dados
5. **Service Layer**: Lógica de negócio isolada em serviços

## Nível de Segurança

### Medidas de Segurança Implementadas

#### 1. Autenticação JWT
- Tokens assinados com HMAC SHA-256
- Validação de expiração automática
- Tokens stateless (sem necessidade de sessão no servidor)
- ✅ **Chave secreta via variável de ambiente (JWT_SECRET)**

#### 2. Criptografia de Senhas
- Senhas armazenadas com BCrypt (hashing one-way)
- Salt automático para cada senha
- Impossibilidade de recuperação da senha original

#### 3. Controle de Acesso Baseado em Roles
- **ADMIN**: Acesso completo a todas as funcionalidades
- **USER**: Acesso limitado baseado em permissões de telas

#### 4. Proteção de Endpoints
- Endpoints públicos: `/api/auth/login`, `/api/auth/cadastro`
- Endpoints protegidos: Requerem token JWT válido
- Endpoints administrativos: Requerem role ADMIN

#### 5. Validação de Dados
- Validação de entrada com Bean Validation
- Validação customizada (ex: CPF)
- Tratamento de erros padronizado

#### 6. CORS Configurado
- ✅ **Whitelist de origens via variável de ambiente (ALLOWED_ORIGINS)**
- Headers expostos configurados adequadamente
- Suporte a credenciais habilitado
- Padrão seguro: apenas localhost em desenvolvimento

#### 7. Rate Limiting
- ✅ **Proteção contra ataques de força bruta**
- Limite de 100 requisições/minuto para endpoints gerais
- Limite de 5 requisições/minuto para login/cadastro
- Resposta HTTP 429 (Too Many Requests) quando excedido
- Headers informativos sobre limites restantes

#### 8. Tratamento de Exceções
- Tratamento centralizado de exceções
- Mensagens de erro padronizadas
- Não exposição de informações sensíveis em erros

#### 9. Spring Security
- Filtros de segurança configurados
- Proteção contra CSRF (desabilitado para API REST)
- Headers de segurança configurados

#### 10. Logs Estruturados
- ✅ **Logs em formato JSON para produção**
- Rastreamento de requisições com Request ID único
- MDC (Mapped Diagnostic Context) para contexto
- Logs de auditoria para operações críticas
- Diferentes níveis de log por ambiente

## Nível de Segurança

### Medidas de Segurança Implementadas

#### 1. Autenticação JWT
- Tokens assinados com HMAC SHA-256
- Validação de expiração automática
- Tokens stateless (sem necessidade de sessão no servidor)

#### 2. Criptografia de Senhas
- Senhas armazenadas com BCrypt (hashing one-way)
- Salt automático para cada senha
- Impossibilidade de recuperação da senha original

#### 3. Controle de Acesso Baseado em Roles
- **ADMIN**: Acesso completo a todas as funcionalidades
- **USER**: Acesso limitado baseado em permissões de telas

#### 4. Proteção de Endpoints
- Endpoints públicos: `/api/auth/login`, `/api/auth/cadastro`
- Endpoints protegidos: Requerem token JWT válido
- Endpoints administrativos: Requerem role ADMIN

#### 5. Validação de Dados
- Validação de entrada com Bean Validation
- Validação customizada (ex: CPF)
- Tratamento de erros padronizado

#### 6. CORS Configurado
- Configuração de CORS para permitir requisições do frontend
- Headers expostos configurados adequadamente

#### 7. Tratamento de Exceções
- Tratamento centralizado de exceções
- Mensagens de erro padronizadas
- Não exposição de informações sensíveis em erros

#### 8. Spring Security
- Filtros de segurança configurados
- Proteção contra CSRF (desabilitado para API REST)
- Headers de segurança configurados

## Endpoints da API

### Autenticação (`/api/auth`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/api/auth/cadastro` | Cadastrar novo usuário | Não |
| POST | `/api/auth/login` | Fazer login e obter token | Não |
| GET | `/api/auth/usuarios` | Listar todos os usuários | ADMIN |
| GET | `/api/auth/usuarios/buscar/{nome}` | Buscar usuários por nome | ADMIN |
| PUT | `/api/auth/usuarios/{id}` | Atualizar usuário | ADMIN |
| DELETE | `/api/auth/usuarios/{id}` | Deletar usuário | ADMIN |
| PUT | `/api/auth/usuarios/{id}/promover-admin` | Promover usuário a admin | ADMIN |
| PUT | `/api/auth/usuarios/{id}/rebaixar-user` | Rebaixar admin a usuário | ADMIN |

### Membros (`/api/membros`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/api/membros` | Cadastrar novo membro | Sim |
| GET | `/api/membros` | Listar todos os membros | Sim |
| GET | `/api/membros/{id}` | Buscar membro por ID | Sim |
| GET | `/api/membros/buscar/nome/{nome}` | Buscar membros por nome | Sim |
| GET | `/api/membros/buscar/cpf/{cpf}` | Buscar membro por CPF | Sim |
| GET | `/api/membros/buscar/ri/{ri}` | Buscar membro por RI | Sim |
| PUT | `/api/membros/{id}` | Atualizar membro | Sim |
| DELETE | `/api/membros/{id}` | Deletar membro | Sim |

### Financeiro (`/api/financeiro`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/api/financeiro` | Cadastrar registro financeiro | Sim |
| GET | `/api/financeiro` | Listar todos os registros | Sim |
| GET | `/api/financeiro/{id}` | Buscar registro por ID | Sim |
| GET | `/api/financeiro/buscar/tipo/{tipo}` | Buscar por tipo | Sim |
| GET | `/api/financeiro/buscar/membro/{membroId}` | Buscar por membro | Sim |
| PUT | `/api/financeiro/{id}` | Atualizar registro | Sim |
| DELETE | `/api/financeiro/{id}` | Deletar registro | Sim |

### Assistência Social (`/api/assistencia-social`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| POST | `/api/assistencia-social` | Cadastrar registro | Sim |
| GET | `/api/assistencia-social` | Listar registros (paginado) | Sim |
| GET | `/api/assistencia-social/{id}` | Buscar registro por ID | Sim |
| PUT | `/api/assistencia-social/{id}` | Atualizar registro | Sim |
| DELETE | `/api/assistencia-social/{id}` | Deletar registro | Sim |

### Permissões (`/api/permissoes`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/api/permissoes/telas` | Listar todas as telas | ADMIN |
| GET | `/api/permissoes/minhas` | Buscar minhas permissões | Sim |
| GET | `/api/permissoes/usuario/{usuarioId}` | Buscar permissões de usuário | ADMIN |
| GET | `/api/permissoes/usuario/{usuarioId}/completo` | Buscar permissões completas | ADMIN |
| PUT | `/api/permissoes/usuario/{usuarioId}` | Atualizar permissões | ADMIN |

### CEP (`/api/cep`)

| Método | Endpoint | Descrição | Autenticação |
|--------|----------|-----------|--------------|
| GET | `/api/cep/{cep}` | Buscar endereço por CEP | Sim |

## Documentação Swagger/OpenAPI

A API possui documentação interativa disponível através do Swagger UI:

- **URL**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Recursos do Swagger

- Visualização de todos os endpoints
- Teste direto dos endpoints na interface
- Autenticação Bearer Token integrada
- Exemplos de requisições e respostas
- Descrições detalhadas de cada endpoint

### Como Usar o Swagger

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Clique em **Authorize** no topo da página
3. Cole o token JWT obtido no login (sem o prefixo "Bearer")
4. Clique em **Authorize** e depois **Close**
5. Agora você pode testar os endpoints diretamente na interface

## Tratamento de Erros

A API retorna erros padronizados no seguinte formato:

```json
{
  "timestamp": "2026-01-28T10:30:00",
  "status": 400,
  "error": "Erro de validação",
  "message": "Campos inválidos",
  "errors": {
    "email": "Email deve ser válido",
    "senha": "Senha é obrigatória"
  }
}
```
## Monitoramento e Métricas

A aplicação inclui Spring Boot Actuator para monitoramento:

- **Health Check**: `http://localhost:8080/actuator/health`
- **Métricas Prometheus**: `http://localhost:8080/actuator/prometheus`

## Estrutura de Resposta Padrão

Todas as respostas bem-sucedidas seguem o formato:

```json
{
  "message": "Mensagem de sucesso",
  "data": {
    // Dados da resposta
  }
}
```

Para respostas paginadas (Assistência Social):

```json
{
  "message": "Registros encontrados com sucesso!",
  "data": [...],
  "currentPage": 0,
  "totalItems": 100,
  "totalPages": 10,
  "pageSize": 10,
  "hasNext": true,
  "hasPrevious": false
}
```

## Desenvolvimento

### Executar Testes

```bash
gradlew test
```

### Gerar Documentação REST Docs

```bash
gradlew asciidoctor
```

### Build para Produção

```bash
gradlew clean build
```

O arquivo WAR será gerado em `build/libs/administrativo-0.0.1-SNAPSHOT.war`
