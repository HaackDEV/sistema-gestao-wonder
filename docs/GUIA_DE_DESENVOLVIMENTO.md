# Guia de Desenvolvimento — WONDER (Back-end)

Este documento é o guia principal para desenvolvedores que vão ler e escrever código neste repositório. Ele complementa o `README.md` com foco estritamente técnico: arquitetura, padrões adotados, convenções, fluxo de desenvolvimento e exemplos práticos.


## Sumário
- Visão Geral da Arquitetura
- Estrutura de Pastas e Camadas
- Padrões Seguidos
- Convenções de Código
- Fluxo de Requisição (da Controller ao Banco)
- Como “Ler” o Projeto (por onde começar)
- Como “Escrever” no Projeto (passo a passo para novos recursos)
- Validações e DTOs
- Mapeamento com MapStruct
- Persistência com JPA/Hibernate
- Tratamento Global de Exceções
- Documentação de API (OpenAPI/Swagger)
- Configurações de Ambiente
- Testes Automatizados
- Dicas de Depuração


## Visão Geral da Arquitetura
O back-end segue uma arquitetura em camadas, separando responsabilidades com foco em manutenção e extensibilidade:
- Resource (Controller): exposição da API HTTP (REST)
- Service: regras de negócio e orquestração
- Repository: persistência de dados (Spring Data JPA)
- Entity: modelo de domínio mapeado com JPA
- DTO/Mapper: contratos de entrada/saída e conversões
- Config/Exceptions: configuração de infraestrutura e tratamento de erros

Tecnologias principais:
- Java 25
- Spring Boot 4.0.3 (Web, Data JPA, Validation)
- PostgreSQL
- Docker/Docker Compose
- Springdoc OpenAPI (Swagger)
- MapStruct (mapeamento DTO <-> Entity)


## Estrutura de Pastas e Camadas
Pacote raiz: `com.haackdev.commercial_management`

- `config/`
  - `OpenApiConfig.java`: metadados do Swagger.
  - `TestConfig.java`: carga de dados para dev (CommandLineRunner).
- `dto/`
  - `request/` e `response/`: objetos de fronteira da API.
- `entity/`
  - Entidades JPA e enums/converters (ex.: `StatusDesenvolvimento`).
- `mapper/`
  - Interfaces MapStruct para conversões DTO/Entity (ex.: `ClienteMapper`).
- `repository/`
  - Interfaces Spring Data JPA (ex.: `ClienteRepository`).
- `resource/`
  - Controladores REST (ex.: `ClienteResource`).
  - `exceptions/`: tratamento global (ex.: `ResourceExceptionHandler`).
- `service/`
  - Regras de negócio por agregado (ex.: `PedidoService`).
- `resources/`
  - `application.properties`: propriedades de execução local.
- `test/`
  - Testes unitários e de camada (ex.: `ClienteResourceTest`).


## Padrões Seguidos
- SOLID: segregação de responsabilidades entre Resource/Service/Repository/Entity.
- Clean Code: nomes semânticos, métodos coesos, baixo acoplamento.
- RESTful: endpoints claros, verbos HTTP corretos, códigos de status consistentes.
- DTO Pattern: isolar contratos externos do domínio interno.
- Mapper Pattern: conversões automáticas e tipadas com MapStruct.
- Global Error Handling: via `@ControllerAdvice` e objetos de erro padronizados.
- Rich Domain Model (onde aplicável): lógica de agregados nas entidades (ex.: `Pedido#getValorTotalCalculado`).
- Repository Pattern: acesso a dados mediado por interfaces.


## Convenções de Código
- Pacotes no singular por agregado: `Cliente`, `Pedido`, `Produto`.
- Controllers com sufixo `Resource` e mapeamento plural: `@RequestMapping("/clientes")`.
- Services com sufixo `Service` injetados por construtor.
- Repositories com sufixo `Repository` extendendo `JpaRepository`.
- DTOs com sufixos `Request` e `Response`.
- Campos obrigatórios marcados com `nullable = false` nas entidades; unicidade com `unique = true` quando aplicável.
- BigDecimal para valores monetários com `precision/scale` definidos.
- Datas com `LocalDate`.
- Endpoints documentados com `@Operation` e `@Tag` (OpenAPI).


## Fluxo de Requisição (da Controller ao Banco)
1. HTTP Request chega ao `Resource` (ex.: `ClienteResource`).
2. `Resource` valida o payload (`@Valid`) e delega ao `Service`.
3. `Service` aplica regras de negócio, usa `Mapper` para converter DTO <-> Entity e chama o `Repository`.
4. O `Repository` persiste/consulta via JPA/Hibernate.
5. O `Service` retorna DTO de saída para o `Resource`.
6. `Resource` monta a `ResponseEntity` com status adequado.
7. Exceções são interceptadas pelo `ResourceExceptionHandler` e retornadas em formato padronizado.


## Como “Ler” o Projeto (por onde começar)
- Para entender um endpoint: abra `resource/*Resource.java` (ex.: `ClienteResource`). Leia as anotações de rota e o `@Operation`.
- Para entender as regras: abra o `Service` correspondente (ex.: `ClienteService`).
- Para ver o contrato da API: confira os DTOs em `dto/request` e `dto/response` e a UI do Swagger.
- Para entender persistência: veja a `Entity` e o `Repository` do agregado.
- Para ver relaciones: analise annotations JPA (`@ManyToOne`, `@OneToMany`, etc.).
- Para erros: veja `resource/exceptions/ResourceExceptionHandler.java`.
- Para exemplos de dados: veja `config/TestConfig.java`.


## Como “Escrever” no Projeto (novo recurso/endpoint)
Suponha criar o recurso `Fornecedor` (analogamente a `Cliente`).

1) Defina a `Entity` em `entity/Fornecedor.java` com JPA:
```java
@Entity
@Table(name = "tb_fornecedor")
public class Fornecedor { /* ...campos e constraints... */ }
```

2) Crie DTOs:
- `dto/request/FornecedorRequest.java` com validações Bean Validation (ex.: `@NotBlank`, `@Size`).
- `dto/response/FornecedorResponse.java` só com dados expostos pela API.

3) Crie o `Mapper` com MapStruct:
```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FornecedorMapper {
  @Mapping(target = "id", ignore = true)
  Fornecedor requestToFornecedor(FornecedorRequest request);
  FornecedorResponse fornecedorToFornecedorResponse(Fornecedor fornecedor);
}
```

4) Crie o `Repository`:
```java
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {}
```

5) Implemente o `Service`:
- Regras de negócio, conversões (DTO<->Entity), e tratamento de exceções de domínio.

6) Exponha no `Resource`:
```java
@RestController
@RequestMapping("/fornecedores")
@Tag(name = "Fornecedores", description = "Endpoints para gerenciamento de fornecedores")
public class FornecedorResource { /* CRUD com ResponseEntity */ }
```

7) Documente com `@Operation` e garanta códigos HTTP corretos (`201 Created` para POST com `Location`).

8) Escreva testes (ver seção Testes Automatizados) e rode.


## Validações e DTOs
- Use Bean Validation em DTOs de entrada (`@NotNull`, `@NotBlank`, `@Size`, etc.).
- `@Valid` no método do `Resource` garante validação automática e 400 em caso de violação.
- Regra: não exponha entidades diretamente na API; utilize `Response DTO`.

Exemplo no `ClienteResource`:
```java
@PostMapping
public ResponseEntity<ClienteResponse> insert(@Valid @RequestBody ClienteRequest request) { /* ... */ }
```


## Mapeamento com MapStruct
- Centralize conversões em `mapper/*Mapper.java`.
- Adote o padrão: `requestTo<Entity>` e `<entity>To<Entity>Response`.
- Configure o `id` para `ignore` na criação via DTO de request, deixando o banco gerar.

Exemplo real (`ClienteMapper.java`):
```java
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClienteMapper {
  @Mapping(target = "id", ignore = true)
  Cliente requestToCliente(ClienteRequest request);
  ClienteResponse clienteToClienteResponse(Cliente cliente);
}
```


## Persistência com JPA/Hibernate
- Use `@ManyToOne`, `@OneToMany`, `@JoinColumn`, `nullable = false` para regras de integridade.
- Utilize `precision`/`scale` em `BigDecimal` para valores monetários.
- Encapsule lógica de agregado na entidade quando fizer sentido (ex.: somatório de itens no `Pedido`).

Exemplo (`Pedido#getValorTotalCalculado`):
```java
public BigDecimal getValorTotalCalculado() {
  BigDecimal total = BigDecimal.ZERO;
  for (ItemPedido item : itens) {
    total = total.add(item.getSubTotal());
  }
  return total;
}
```


## Tratamento Global de Exceções
- `@ControllerAdvice` centraliza respostas de erro com semântica HTTP.
- Utilize exceções de domínio (ex.: `ResourceNotFoundException`, `DatabaseException`).
- Mensagens padronizadas e timestamps (`StandardError`).

Exemplo (`ResourceExceptionHandler`):
```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest req) {
  HttpStatus status = HttpStatus.NOT_FOUND;
  StandardError err = new StandardError(Instant.now(), status.value(), "Recurso não encontrado", e.getMessage(), req.getRequestURI());
  return ResponseEntity.status(status).body(err);
}
```


## Documentação de API (OpenAPI/Swagger)
- Configurada em `config/OpenApiConfig.java`.
- UI disponível em `/swagger-ui.html`.
- Anote endpoints com `@Tag` e `@Operation` para enriquecer a doc.


## Configurações de Ambiente
- `src/main/resources/application.properties` contém a configuração local padrão:
  - URL Banco: `jdbc:postgresql://localhost:5432/gestao_comercial`
  - Usuário/Senha: `admin` / `12345`
  - JPA: `spring.jpa.hibernate.ddl-auto=create` (somente dev!)
  - Swagger: caminhos em `springdoc.*`
- `docker-compose.yml` provê o Postgres para desenvolvimento.
- Para subir DB: `docker-compose up -d`

Atenção: para ambientes não-dev, ajuste `ddl-auto` para `validate`/`none` e configure migrações (Flyway/Liquibase) conforme necessidade.


## Testes Automatizados
- Testes por camada em `src/test/java/...` para `resource` e `service`.
- Utilize `@SpringBootTest` ou testes slice (ex.: `@WebMvcTest`) quando aplicável.
- Execute via Maven/IDE. Exemplos prontos: `ClienteResourceTest`, `PedidoServiceTest`, etc.
- Dicas:
  - Isolar regras no `Service` facilita testes unitários.
  - Mockar dependências de `Service` quando necessário.


## Dicas de Depuração
- Ative `spring.jpa.show-sql=true` para inspecionar SQL.
- Use logs do Spring (`DEBUG`/`TRACE`) conforme necessidade.
- Explore `config/TestConfig.java` para dados de amostra que ajudam a reproduzir cenários.


---
Este guia cobre o que você precisa para navegar (ler) e contribuir (escrever) de forma consistente com os padrões deste repositório. Em caso de dúvidas, verifique as classes análogas já existentes e mantenha as convenções aqui descritas.