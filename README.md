# Sistema Bancário - Projeto Ada

## Descrição
Sistema bancário completo desenvolvido em Kotlin com Spring Boot, incluindo frontend web para cadastro e login de clientes.

## Tecnologias Utilizadas
- **Backend**: Kotlin + Spring Boot 3.2.5
- **Frontend**: HTML5, CSS3, JavaScript
- **Banco de Dados**: H2 Database (em memória)
- **Build Tool**: Gradle
- **Java**: JDK 24

## Funcionalidades
- ✅ Cadastro de clientes com dados pessoais completos
- ✅ Sistema de login com validação
- ✅ Interface web responsiva com design bancário
- ✅ API REST para gerenciamento de clientes
- ✅ Banco de dados H2 integrado
- ✅ Validação de CPF e email

## Estrutura do Projeto
```
src/
├── main/
│   ├── kotlin/com/example/demo/
│   │   ├── DemoApplication.kt          # Classe principal
│   │   ├── entity/
│   │   │   ├── Client.kt               # Entidade Cliente
│   │   │   └── Account.kt              # Entidade Conta
│   │   ├── repository/
│   │   │   ├── ClientRepository.kt     # Repositório de Clientes
│   │   │   └── AccountRepository.kt    # Repositório de Contas
│   │   └── controller/
│   │       └── ClientController.kt     # Controlador REST
│   └── resources/
│       ├── application.yml             # Configurações
│       └── static/
│           ├── index.html              # Página inicial
│           ├── cadastro.html           # Página de cadastro
│           ├── login.html              # Página de login
│           └── js/
│               ├── cadastro.js         # Scripts do cadastro
│               └── login.js            # Scripts do login
```

## Como Executar

### Pré-requisitos
- Java JDK 24 ou superior
- Gradle (incluído via wrapper)

### Executando o Projeto
1. Clone este repositório
2. Navegue até o diretório do projeto
3. Execute o comando:
   ```bash
   ./gradlew bootRun
   ```
   No Windows PowerShell:
   ```powershell
   .\gradlew bootRun
   ```

### Acessando o Sistema
Após executar, acesse:
- **Página Inicial**: http://localhost:8080/
- **Cadastro**: http://localhost:8080/cadastro.html
- **Login**: http://localhost:8080/login.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (deixe em branco)

## API Endpoints

### Clientes
- `POST /api/clients` - Criar novo cliente
- `GET /api/clients/{id}` - Buscar cliente por ID
- `GET /api/clients/cpf/{cpf}` - Buscar cliente por CPF
- `GET /api/clients/email/{email}` - Buscar cliente por email

### Exemplo de Requisição (Cadastro)
```json
{
  "name": "João Silva",
  "cpf": "12345678901",
  "email": "joao@email.com",
  "birthDate": "1990-01-01",
  "phone": {
    "countryCode": "+55",
    "areaCode": "11",
    "numberCode": "987654321"
  },
  "address": {
    "street": "Rua das Flores, 123",
    "number": "123",
    "complement": "Apto 45",
    "neighborhood": "Centro",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01234-567"
  }
}
```

## Características Técnicas
- **Arquitetura**: MVC com separação clara de responsabilidades
- **Segurança**: Validações de entrada e sanitização de dados
- **Responsividade**: Interface adaptável a diferentes dispositivos
- **Persistência**: Dados salvos em banco H2 com persistência em arquivo
- **Logs**: Sistema de logging integrado
- **Tratamento de Erros**: Respostas HTTP apropriadas e mensagens de erro

## Banco de Dados
O sistema utiliza H2 Database com as seguintes tabelas:
- **CLIENT**: Dados dos clientes
- **ACCOUNTS**: Contas bancárias (uma por cliente)

## Desenvolvimento
Desenvolvido como projeto educacional para demonstrar:
- Desenvolvimento full-stack com Kotlin/Spring Boot
- Integração frontend-backend
- Boas práticas de desenvolvimento web
- Arquitetura REST
- Persistência de dados

## Autor
Desenvolvido por Wellington - Projeto Ada

## Licença
Este projeto é para fins educacionais.
