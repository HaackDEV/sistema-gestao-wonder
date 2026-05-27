> Aviso de Transparência (IA)
> Este documento foi gerado com auxílio de uma ferramenta de IA em 2026-05 e reflete sugestões de revisão à época.
> Ele NÃO deve ser interpretado como estado atual e oficial do código.
> Para informações atualizadas, consulte o README e o GUIA_DE_DESENVOLVIMENTO.

# Análise Técnica e Revisão de Código - Backend MVP

Este documento registra a revisão técnica realizada no estágio final da Fase 1 (Fundação) do projeto WONDER. O objetivo é estabelecer diretrizes de qualidade, identificar pontos de refatoração necessários e alinhar o projeto a padrões de mercado.

## 1. Arquitetura e Design REST

### [Ponto Crítico] Acoplamento de Entidades
**Observação:** A API utiliza classes de Entidades JPA diretamente nos Resources (Controllers).
- **Problema:** Exposição desnecessária de campos de auditoria, IDs internos e relacionamentos cíclicos ao Frontend.
- **Risco:** Mudanças na tabela do banco quebram o contrato da API imediatamente.
- **Solução:** Implementação de DTOs (Data Transfer Objects) e Mappers (MapStruct).

### [Melhoria] Injeção de Dependências
**Observação:** Uso predominante de Field Injection (`@Autowired` em atributos privados).
- **Problema:** Dificulta a criação de mocks em testes unitários e não garante o estado final das beans.
- **Solução:** Migração para Constructor Injection usando `@RequiredArgsConstructor` do Lombok.

## 2. Padrões Clean Code e SOLID

### Modelo de Domínio Rico (Domínio vs Serviço)
**Observação:** Algumas lógicas de integridade (como o vínculo bidirecional entre Pedido e Itens) estão espalhadas pelos Services.
- **Sugestão:** Fortalecer o encapsulamento nas Entidades. Métodos como `addItem(Item item)` devem gerenciar a consistência em memória.

### Segregação de Responsabilidades (SRP)
- **Status:** Boas divisões de pacotes (Resource, Service, Repository, Entity).
- **Próximo Passo:** Isolar a lógica de conversão de `Desenvolvimento` -> `Pedido` em um componente ou serviço especializado para não sobrecarregar o `DesenvolvimentoService`.

## 3. Validação e Segurança de Dados

### Bean Validation (Jakarta)
**Observação:** Falta de validações a nível de input (`@NotBlank`, `@Size`, `@Email`).
- **Problema:** Erros de validação só ocorrem ao tentar persistir no banco (Erro 500), em vez de serem tratados prontamente na entrada (Erro 400).
- **Solução:** Adicionar anotações de validação nos DTOs e habilitar `@Valid` nos controllers.

## 4. Documentação Técnica (Swagger)

- **Status:** Implementado (Springdoc OpenAPI).
- **Refino:** Enriquecer os Schemas e as descrições de resposta (ex: detalhar o que cada código HTTP significa especificamente para cada recurso).

---

## Próximas Etapas de Qualidade (Backlog de Engenharia)

1.  **Refatoração para DTOs**: Isolar o contrato da API.
2.  **Validação de Bean**: Garantir a higiene dos dados.
3.  **Injeção por Construtor**: Estabilidade e testabilidade.
4.  **Testes de Integração**: Expandir a cobertura para cenários de erro detalhados.