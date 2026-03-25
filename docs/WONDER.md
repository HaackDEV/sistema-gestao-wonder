# Sistema de Gestão WONDER

<aside>
📌

**Objetivo desta página**

Centralizar as informações do sistema ***WONDER*** para entender rapidamente o contexto, a stack, o banco e o plano de entrega do MVP.

</aside>

## Visão geral

O ***WONDER*** é um **sistema de gerenciamento comercial** para substituir planilhas e controles manuais, com foco em fluxo de cadastro e operação (clientes, fornecedores, produtos, desenvolvimentos e pedidos).

## Stack

- **Java 25**
- **Spring Boot 4.0.3**
- **PostgreSQL**

## Repositórios e ambientes

- **Backend (API):**
- **Frontend (React):**
- **Ambientes:** Dev / Homolog / Prod

<aside>
✅

Preencha os links acima quando já estiverem definidos (GitHub, Railway/Render/Fly, Vercel/Netlify etc.).

</aside>

---

## Estrutura do banco de dados

### Diagrama (referência)

![1a28056e-9993-47f5-bfca-68e2d2875851.jpeg](../../Users/ruanh/Downloads/1a28056e-9993-47f5-bfca-68e2d2875851.jpeg)

### Regras e padrões (definições do time)

- **Nomenclatura:** snake_case no banco e nas URLs de recursos (ex: `/itens_pedidos`), camelCase no Java.
- **Constraints:** PK/FK obrigatórias, `NOT NULL` onde fizer sentido, índices nas FKs e campos de busca.
- **Integridade:** exclusões com regra clara (soft delete vs cascade) antes de implementar.

---

## Planejamento do MVP (Roadmap)

### ⚙️ Fase 1: Fundação Backend (Spring Boot + PostgreSQL)

*Objetivo: Construir a API REST robusta, mapear todas as tabelas do diagrama e garantir a integridade dos dados.*

- [x]  **Etapa 1.1:** Setup do banco de dados e mapeamento ORM inicial.
- [x]  **Etapa 1.2:** CRUD de `Fornecedores` + Tratamento Global de Exceções (`ResourceExceptionHandler`).
- [x]  **Etapa 1.3:** CRUD de `Clientes` (Entidade independente, focar no mapeamento dos vários atributos de contato e endereço).
- [x]  **Etapa 1.4:** CRUD de `Produtos` (Implementar a primeira Chave Estrangeira com `@ManyToOne` apontando para Fornecedor).
- [x]  **Etapa 1.5:** Módulo de `Desenvolvimentos` (CRUD e relacionamentos básicos implementados).
- [x]  **Etapa 1.6:** Módulo de `Pedidos` e `Itens_Pedido` (CRUD e relacionamento bidirecional configurado).
- [x]  **Pendência (Próxima Sessão):** Refatorar `Pedido` para Modelo de Domínio Rico (Clean Code).
    *   Mover cálculo de `valorTotal` para a entidade `Pedido` (`getValorTotalCalculado()`).
    *   Implementar métodos de associação bilateral na entidade (`addItem()`).
    *   Garantir recálculo automático do total no `PedidoService` (insert/update).
- [x]  **Pendência:** Implementar lógica de conversão automática de `Desenvolvimento` -> `Pedido`.

### 📝 Detalhamento das Pendências (Fase 1)

#### **1. Inteligência do Pedido (Cálculos e Integridade - Clean Code)**
*   **Contexto:** O `Pedido` deve ser um modelo rico, responsável por sua própria consistência interna.
*   **Próximos Passos Técnicos:**
*   **Entidade Pedido:** Criar método `getValorTotalCalculado()` que soma os subtotais de cada `ItemPedido`.
*   **Associação:** Criar método `addItem(ItemPedido item)` em `Pedido` para garantir que o vínculo bidirecional seja feito corretamente em memória antes de persistir.
*   **Service:** Ajustar `PedidoService.insert()` e `PedidoService.update()` para que o `valorTotal` persistido seja sempre derivado do cálculo da entidade, evitando divergências.
*   **Garantia de Integridade:** Usar `@Transactional` em todos os métodos que alteram `Pedido` e `ItemPedido` simultaneamente.
#### **2. Fluxo de Conversão (Desenvolvimento -> Pedido)**
*   **Contexto:** Evitar redigitação. Se um `Desenvolvimento` for aprovado, o sistema gera um `Pedido` base.
*   **Fluxo Sugerido:**
    *   Novo endpoint: `POST /desenvolvimentos/{id}/converter`.
    *   Verificar se `status == StatusDesenvolvimento.APROVADO`.
    *   Criar nova instância de `Pedido` com os dados do `Cliente` e `Produto` do desenvolvimento.
    *   Marcar `virou_pedido = true` e registrar `data_conversao` no registro de origem.

---

### 🖥️ Fase 2: Construção da Interface (Frontend com React)

*Objetivo: Criar as telas que a cliente vai usar no dia a dia para substituir planilhas e controles manuais.*

- [ ]  **Etapa 2.1:** Setup do projeto React, roteamento de páginas e layout base (menu lateral, cabeçalho).
- [ ]  **Etapa 2.2:** Telas de Cadastro/Listagem (Fornecedores, Clientes, Produtos).
- [ ]  **Etapa 2.3:** Conexão com a API (Axios, CORS, exibição de erros 400/404).
- [ ]  **Etapa 2.4:** Telas de Operação (painel de Desenvolvimentos + emissão/visualização de Pedidos).

### 🔒 Fase 3: Segurança e Refinamento

*Objetivo: Proteger os dados da agência contra acessos indevidos e polir o sistema.*

- [ ]  **Etapa 3.1:** Spring Security + JWT.
- [ ]  **Etapa 3.2:** Tela de Login + rotas protegidas.
- [ ]  **Etapa 3.3:** Filtros de busca (ex: status = "Aprovado").

### 🚀 Fase 4: Implantação (Deploy)

*Objetivo: Tirar do `localhost` e colocar em produção para acesso da cliente.*

- [ ]  **Etapa 4.1:** PostgreSQL em nuvem.
- [ ]  **Etapa 4.2:** Deploy da API (Railway, Render ou [Fly.io](http://Fly.io)).
- [ ]  **Etapa 4.3:** Deploy do Frontend (Vercel ou Netlify) apontando para a API.

---

## Método de trabalho (Ágil)

[Kanban — WONDER](https://www.notion.so/befee5a8076a4ac0b8e79a2215c23193?pvs=21)

### Sugestão: Kanban com cadência semanal (simples e eficiente)

- **Quadro Kanban:** Backlog → Em andamento → Em revisão → Teste → Done
- **WIP limit:** limite de itens “Em andamento” para reduzir troca de contexto.

### Definição de pronto (DoD)

- Endpoint funcionando
- Validações + tratamento de erros coerentes
- Teste mínimo (unitário ou integração, conforme o caso)
- Documentado (README/Swagger)

---

## Próximas decisões (pendentes)

- Estratégia de **soft delete** (sim/não)
- Onde ficará a **documentação da API** (Swagger/OpenAPI)
- Regra de negócio: O Pedido deve herdar o preço do Produto no momento da venda ou permitir edição livre?
- Como rastrear o histórico de alterações em um Pedido (Audit Log simplificado).
