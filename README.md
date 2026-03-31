# WONDER - Sistema de Gestão Comercial 🚀

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-green?style=for-the-badge&logo=springboot)
![Postgres](https://img.shields.io/badge/Postgres-15+-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)

Um ERP especializado para representantes comerciais autônomos. Ele automatiza o fluxo de vendas, desde o desenvolvimento de amostras até a emissão de pedidos finais, substituindo controles manuais por uma API robusta, performática e escalável.

---

## 🏛️ Arquitetura e Engenharia

Este projeto foi construído seguindo rigorosos padrões de engenharia de software para garantir manutenibilidade e extensibilidade:

- **SOLID Principles**: Responsabilidades segregadas em camadas (Resource, Service, Repository, Entity).
- **Clean Code**: Nomenclatura semântica e métodos autodocumentados.
- **Rich Domain Model**: Lógicas de negócio (como cálculos de subtotal e integridade bidirecional) encapsuladas nas entidades.
- **RESTful API**: Endpoints padronizados com suporte a documentação Swagger/OpenAPI.
- **Global Error Handling**: Tratamento centralizado de exceções para respostas HTTP semânticas.

## 🚀 Funcionalidades Atuais (MVP)

- [x] **Gestão de Fornecedores**: Cadastro completo com vínculos de produtos.
- [x] **Gestão de Clientes**: Dados de faturamento, contato e condições comerciais.
- [x] **Catálogo de Produtos**: Controle de custos, venda e materiais.
- [x] **Módulo de Desenvolvimento**: Acompanhamento de amostras e pilotagens (com conversão automática para pedidos).
- [x] **Emissão de Pedidos**: Fluxo de venda com cálculo automático de impostos e totais.

## 🛠️ Stack Tecnológica

- **Backend**: Java 25, Spring Boot 4.0.3, Spring Data JPA.
- **Validação**: Bean Validation (Jakarta Validation).
- **Documentação**: Springdoc OpenAPI (Swagger).
- **Banco de Dados**: PostgreSQL.
- **Containerização**: Docker e Docker Compose.

---

## 📖 Documentação Adicional

Para entender mais sobre os processos de engenharia do projeto, acesse:
- [📌 Roadmap Detalhado (WONDER.md)](docs/WONDER.md)
- [🔍 Relatório de Revisão Técnica (Backend Review)](docs/backend_review.md)

---

## 🚦 Como Rodar em Desenvolvimento

1. **Pré-requisitos**: Docker e Java 25 instalados.
2. **Banco de Dados**: Execute `docker-compose up -d`.
3. **Aplicação**: Rode via IDE ou `./mvnw spring-boot:run`.
4. **API Docs**: Acesse `http://localhost:8080/swagger-ui.html`.

---
*Developed with focus on quality and commercial scalability.*
