# WONDER - Master Roadmap 🗺️

Este documento centraliza o planejamento estratégico, as diretrizes de engenharia e o progresso do desenvolvimento do sistema **WONDER**.

---

## 🏗️ Filosofia de Desenvolvimento e Stack

- **Linguagem:** Java 25 (Funcionalidades modernas da JVM)
- **Framework:** Spring Boot 4.0.3 (Ecossistema Spring de última geração)
- **Engine de Banco:** PostgreSQL (Gerenciamento transacional robusto)
- **Engenharia:** DDD Simplificado (Entidades Ricas), Clean Code e SOLID.

---

## 📈 Roadmap de Entrega

### ✅ Fase 1: Fundação Backend (MVP Funcional)
*Objetivo: Estabelecer a persistência e as operações básicas (CRUD) de todos os domínios essenciais.*

- [x]  **Etapa 1.1:** Setup de infraestrutura (Postgres + Hibernate + Docker).
- [x]  **Etapa 1.2:** Domínio de `Fornecedores` e `Clientes`.
- [x]  **Etapa 1.3:** Domínio de `Produtos` com vínculos relacionais.
- [x]  **Etapa 1.4:** Módulo de `Desenvolvimentos` (Acompanhamento de amostras).
- [x]  **Etapa 1.5:** Módulo de `Pedidos` (Emissão e integridade bidirecional).
- [x]  **Etapa 1.6:** Implementação do Cálculo Automático do Valor Total (`getValorTotalCalculado`).
- [x]  **Etapa 1.7:** Lógica de Conversão Automática de `Desenvolvimento` -> `Pedido`.
- [x]  **Review Técnica Sênior:** Concluída (Ver [backend_review.md](backend_review.md)).

### 🚧 Fase 2: Qualidade e Refatoração (Concluída para Domínios Base)
*Objetivo: Profissionalizar o contrato da API, isolar o domínio e garantir a integridade total dos dados.*

- [x]  **Etapa 2.1:** Implementação da Camada de **DTOs** (Isolamento do Banco da API).
- [x]  **Etapa 2.2:** Integração do **MapStruct** para mapeamento performático.
- [x]  **Etapa 2.3:** Implementação do **Bean Validation** em todos os inputs e Tratamento Global de Exceções 422.
- [x]  **Etapa 2.4:** Refatoração para **Constructor Injection** em Services e Resources.
- [ ]  **Etapa 2.5:** Setup de Variáveis de Ambiente para Produção.

### 📖 Fase 3: Documentação de Qualidade e Testes (CI/CD Ready)
*Objetivo: Alcançar cobertura de testes e documentação de integração completa.*

- [x]  Configurar Springdoc Swagger UI (Documentação Interativa).
- [x]  Testes de Unidade (`Service Layer`).
- [x]  Testes de Integração (`Controller Layer` com `@WebMvcTest`).
- [ ]  Documentação de Schemas OpenAPI detalhados.

### 🚀 Fase 4: Implantação e Nuvem (Cloud Architecture)
*Objetivo: Publicar a API em ambiente escalável.*

- [ ]  Migração do PostgreSQL para nuvem (Supabase/Neon).
- [ ]  Deploy automático (CI/CD) no Railway ou Render.

### 🖥️ Fase 5: Interface Gráfica (Frontend React)
*Objetivo: Dar vida ao sistema com uma UI moderna e responsiva.*

- [ ]  Setup React (Vite) + Tailwind CSS.
- [ ]  Integração com a API Backend (Axios).
- [ ]  Dashboard de Operações e Gestora Comercial.

### 🛡️ Fase 6: Segurança e Melhorias Contínuas (Pós-Lançamento)
*Objetivo: Elevar a arquitetura do projeto para padrões corporativos após a primeira versão estar no ar.*

- [ ] Implementação de Segurança com **Spring Security** e tokens **JWT**.
- [ ] Controle de acesso baseado em Roles (Admin vs Representante).
- [ ] Versionamento estrutural de Banco de Dados com **Flyway**.

---

## 🛠️ Padrões e Decisões Técnicas

- **Case Style:** `snake_case` em tabelas e URLs; `camelCase` no código Java.
- **API Versioning:** Versão inicial no header ou URL conforme necessidade futura.
- **Relacionamentos:** Uso de `CascadeType.ALL` em Pedidos para integridade total dos itens.
- **Lazy Loading:** Padrão para coleções grandes, evitando sobrecarga de memória.

---
*Este roadmap é um documento vivo e deve ser atualizado a cada sprint de desenvolvimento.*
