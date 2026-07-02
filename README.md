# CommerceFlow — Sistema de Gestão Comercial

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)
![Railway](https://img.shields.io/badge/Deploy-Railway-black?style=for-the-badge&logo=railway)

> API REST para gestão comercial de representantes autônomos — controle de fornecedores, clientes, produtos, amostras e pedidos com conversão automática de desenvolvimento em pedido.

**🌐 [Swagger UI — Live Demo](https://sistema-gestao-wonder-production.up.railway.app/swagger-ui/index.html)**

---

## 💡 Problema que resolve

Representantes comerciais autônomos gerenciam amostras, pilotagens e pedidos de forma manual — planilhas, cadernos e WhatsApp. O **CommerceFlow** centraliza esse fluxo em uma API, automatizando desde o cadastro de amostras até a emissão de pedidos com cálculo automático de totais.

---

## 🚀 Funcionalidades

- **Fornecedores** — Cadastro e vínculo com produtos
- **Clientes** — Dados de faturamento, contato e condições comerciais
- **Produtos** — Catálogo com preço de custo, venda e materiais
- **Desenvolvimentos** — Acompanhamento de amostras e pilotagens
- **Pedidos** — Emissão com cálculo automático de totais
- **Conversão automática** — Desenvolvimento → Pedido com um endpoint (retorna `201 Created + Location`)

---

## 🛠️ Stack & Decisões Técnicas

| Camada | Tecnologia | Por que escolhi? (Decisão Técnica) |
|---|---|---|
| **Linguagem** | Java 25 | Uso de features modernas (Records, Pattern Matching) para código limpo e conciso. |
| **Framework** | Spring Boot 4.0.3 | Ecossistema maduro, produtividade e injeção de dependências robusta. |
| **Persistência** | Spring Data JPA + Hibernate 7 | Abstração eficiente para acesso a dados e mapeamento objeto-relacional (ORM). |
| **Banco** | PostgreSQL 17 (Supabase) | Integridade referencial, robustez e suporte avançado para dados complexos. |
| **Validação** | Jakarta Bean Validation | Garantia de integridade dos dados na entrada da API (*Fail-fast validation*). |
| **Mapeamento** | MapStruct 1.6 | Conversão de DTOs em tempo de compilação, garantindo alta performance e segurança de tipos (*type-safety*). |
| **Documentação** | Springdoc OpenAPI 3 (Swagger UI) | Contrato de API vivo e interativo, facilitando a integração para clientes/front-end. |
| **Containers** | Docker + Docker Compose | Padronização de ambiente (o código funciona em qualquer máquina) e onboarding rápido. |
| **Deploy** | Railway (Dockerfile multi-stage) | CI/CD fluido com imagens Docker otimizadas (multi-stage) para deploy mais rápido e leve. |
| **Testes** | JUnit 5 + Mockito | Garantia de qualidade com testes unitários focados nas regras de negócio e testes de integração. |

---

## 🏛️ Arquitetura & Boas Práticas

Para garantir um código escalável e de fácil manutenção, o projeto segue o fluxo:
```
Resource (Controller) → Service → Repository → Entity
```

- **Princípios SOLID** — Responsabilidades estritamente segregadas por camada, facilitando testes e evolução.
- **DTOs com Java Records** — Isolamento total entre o contrato da API e o modelo do banco de dados, garantindo imutabilidade.
- **Constructor Injection** — Evita problemas de testes e acoplamento gerados pelo uso de `@Autowired` em propriedades (*Field Injection*).
- **Rich Domain Model** — Regras de negócio essenciais são encapsuladas nas próprias entidades, evitando o antipattern de *Anemic Domain Model*.
- **Global Exception Handler (`@RestControllerAdvice`)** — Padronização de erros da API com respostas HTTP semânticas (400, 404, 422, 500) e tratadas de forma centralizada.
- **Idempotência no Seed** — O `DataSeedConfig` garante que a população de dados inicial (cargos, configurações) seja segura e não cause duplicações em novos deploys.

---

## ⚙️ Como rodar localmente

**Pré-requisitos:** Docker e Java 25

```bash
# 1. Clone o repositório
git clone https://github.com/HaackDEV/sistema-gestao-wonder.git
cd sistema-gestao-wonder

# 2. Configure as variáveis de ambiente
cp .env.example .env
# Edite o .env com seus valores

# 3. Suba o banco
docker compose up -d

# 4. Rode a aplicação
./mvnw spring-boot:run        # Linux/macOS
mvnw.cmd spring-boot:run      # Windows

# 5. Acesse o Swagger
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testes

```bash
./mvnw test
```

Cobertura: camada de serviço (unitários com Mockito) e camada web (integração com @WebMvcTest).

---

## 📖 Documentação adicional

- [📌 Roadmap do projeto (COMMERCEFLOW.md)](docs/COMMERCEFLOW.md)
- [🔍 Revisão técnica (backend_review.md)](docs/backend_review.md)
