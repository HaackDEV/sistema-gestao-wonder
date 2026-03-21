# WONDER - Sistema de Gestão Comercial

Sistema de gerenciamento para representantes comerciais autônomos, focado em substituir controles manuais por um fluxo automatizado de clientes, fornecedores, produtos e pedidos.

## 🚀 Tecnologias

- **Java 25** (Utilizando as features mais recentes da JVM)
- **Spring Boot 4.0.3**
- **PostgreSQL** (Banco de dados relacional robusto)
- **Docker** (Ambiente de desenvolvimento isolado)
- **Maven** (Gerenciamento de dependências)

## 📌 Status do Projeto e Roadmap

O projeto está em fase ativa de desenvolvimento. Atualmente, a **Fase 1 (Fundação Backend)** está concluída em sua estrutura básica.

- [x] CRUD de Fornecedores, Clientes e Produtos.
- [x] Módulo de Desenvolvimentos (Amostras e Pilotagens).
- [x] Módulo de Pedidos e Itens de Pedido.
- [ ] Implementação de Regras de Negócio Avançadas (Soma automática e Conversão).

**Acesse a documentação detalhada aqui:** [Documentação do Projeto (WONDER.md)](docs/WONDER.md)

## 🛠️ Como Executar o Projeto

1. Certifique-se de ter o **Docker** instalado.
2. Clone o repositório.
3. Execute o comando `docker-compose up -d` para subir o banco de dados.
4. Execute o projeto via sua IDE preferida ou via comando `./mvnw spring-boot:run`.
5. A API estará disponível em `http://localhost:8080`.

---
*Desenvolvido por Ruan Haack*
