# Banco ADA - Sistema Bancário Digital

Sistema bancário desenvolvido com Spring Boot e Kotlin que permite cadastro de clientes e gerenciamento de contas bancárias.

## Tecnologias Utilizadas

- **Backend**: Spring Boot 3.2.5, Kotlin 1.9.22
- **Banco de Dados**: H2 Database (arquivo)
- **Frontend**: HTML, CSS, JavaScript, Bootstrap 5
- **APIs REST**: Endpoints para gerenciamento de clientes e contas

## Funcionalidades

- Cadastro de novos clientes
- Criação automática de conta bancária ao cadastrar cliente
- Login de usuários
- Interface moderna com estilo bancário profissional
- Armazenamento persistente de dados

## Estrutura do Projeto

- **Controller**: Endpoints REST para operações CRUD
- **Service**: Lógica de negócios para clientes e contas
- **Repository**: Interfaces de acesso a dados
- **Model**: Entidades do sistema (Client, Account)
- **DTO**: Objetos de transferência de dados
- **Frontend**: Páginas HTML responsivas com JavaScript

## Como Executar

1. Clone o repositório
2. Execute o projeto com: `./gradlew bootRun`
3. Acesse a aplicação em: `http://localhost:8081`

## Telas do Sistema

- **Página Inicial**: Visão geral dos serviços bancários
- **Login**: Acesso à conta via email e CPF
- **Cadastro**: Registro de novos clientes com informações pessoais

## Autor

Desenvolvido como parte do projeto para ADA.
