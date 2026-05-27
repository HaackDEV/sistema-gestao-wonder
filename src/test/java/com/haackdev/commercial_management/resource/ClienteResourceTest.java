package com.haackdev.commercial_management.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haackdev.commercial_management.dto.request.ClienteRequest;
import com.haackdev.commercial_management.dto.response.ClienteResponse;
import com.haackdev.commercial_management.service.ClienteService;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração da Camada Web (Controller) para a entidade Cliente.
 * Usa Mocks (MockitoBean) para isolar a camada de serviço.
 */
@WebMvcTest(ClienteResource.class)
public class ClienteResourceTest {

    @Autowired
    private MockMvc mockMvc; // Ferramenta do Spring para simular requisições HTTP

    @MockitoBean
    private ClienteService service; // Mock da camada de serviço. Em Spring Boot 3.4+ usa-se @MockitoBean ao invés de @MockBean

    private final ObjectMapper objectMapper = new ObjectMapper(); // Ferramenta para traduzir objetos Java em JSON (Serialização/Desserialização)

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente; // ID usado para simular um cliente atrelado a algo que não pode ser apagado
    private ClienteRequest requestDTO;

    /**
     * Preparação inicial, ocorre ANTES de cada método de teste ser executado.
     * Serve para definir o comportamento das nossas simulações (Mocks).
     */
    @BeforeEach
    void setUp() {
        idExistente = 1L;
        idInexistente = 99L;
        idDependente = 2L;

        ClienteResponse responseDTO = new ClienteResponse(
                1L,
                "Razão Social",
                "Wonder SA",
                "12345678000190",
                "email@teste.com",
                "Cidade",
                "SC"
        );

        requestDTO = new ClienteRequest(
                "Razão Social",
                "Wonder SA",
                "12345678000190",
                "Endereço",
                "Cidade",
                "SC",
                "4799999999",
                "email@teste.com",
                "Contato Dev",
                "Contato Compras",
                "30 dias"

        );

        // Regra do mock: Quando o findAll() for chamado, retorne uma lista com nosso cliente.
        when(service.findAll()).thenReturn(List.of(responseDTO));

        // Regra do mock: Quando buscar por ID 1, retorne o cliente.
        when(service.findById(idExistente)).thenReturn(responseDTO);
        
        // Regra do mock: Quando buscar por ID 99, lance exceção (Não encontrado).
        when(service.findById(idInexistente)).thenThrow(ResourceNotFoundException.class);

        // Regra do mock: Quando salvar qualquer cliente, retorne esse mesmo cliente salvo.
        when(service.insert(any(ClienteRequest.class))).thenReturn(responseDTO);

        // Regra do mock: Quando atualizar o ID existente, retorna ele mesmo. Modos inválidos laçam exceção.
        when(service.update(eq(idExistente), any(ClienteRequest.class))).thenReturn(responseDTO);
        when(service.update(eq(idInexistente), any(ClienteRequest.class))).thenThrow(ResourceNotFoundException.class);

        // Regras do mock: Deleção. doNothing é usado para métodos void.
        doNothing().when(service).delete(idExistente);
        doThrow(ResourceNotFoundException.class).when(service).delete(idInexistente);
        doThrow(DatabaseException.class).when(service).delete(idDependente); // Simula Constraint Violation (Tentou excluir algo atrelado)
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE GET
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarListaDeClientesEStatusOk() throws Exception {
        // ACT (Ação a ser testada - Bater no Endpoint com GET)
        ResultActions resultado = mockMvc.perform(get("/clientes")
                .accept(MediaType.APPLICATION_JSON));

        // ASSERT (Verificações)
        resultado.andExpect(status().isOk()); // Esperamos HTTP 200 OK
        resultado.andExpect(jsonPath("$[0].id").value(idExistente)); // Esperamos que o id do primeiro item seja 1
        resultado.andExpect(jsonPath("$[0].nomeFantasia").value("Wonder SA"));
    }

    @Test
    public void findByIdDeveRetornarClienteEStatusOkQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/clientes/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.nomeFantasia").value("Wonder SA"));
    }

    @Test
    public void findByIdDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/clientes/{id}", idInexistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound()); // Esperamos HTTP 404 (Tratado pelo ExceptionHandler)
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE POST
    // -------------------------------------------------------------

    @Test
    public void insertDeveRetornarStatusCreatedEClienteAoInserir() throws Exception {
        // Converte nosso objeto Java em um JSON para enviarmos na requisição HTTP
        String corpoJson = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(post("/clientes")
                .content(corpoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isCreated()); // HTTP 201
        resultado.andExpect(header().exists("Location")); // Header URI foi adicionado na resposta?
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.nomeFantasia").value("Wonder SA"));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE DELETE
    // -------------------------------------------------------------

    @Test
    public void deleteDeveRetornarNoContentQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/clientes/{id}", idExistente));

        resultado.andExpect(status().isNoContent()); // HTTP 204 (Deleção sem retorno de dados)
    }

    @Test
    public void deleteDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/clientes/{id}", idInexistente));

        // Note: Se o serviço for robusto isso deve ser tratado.
        resultado.andExpect(status().isNotFound()); // HTTP 404
    }

    @Test
    public void deleteDeveRetornarBadRequestQuandoHouverDependenciaNoBanco() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/clientes/{id}", idDependente));

        // Esperançosamente HTTP 400 da restrição do DB Exception Handler
        resultado.andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE PUT
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarClienteEStatusOkQuandoIdExistir() throws Exception {
        String corpoJson = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(put("/clientes/{id}", idExistente)
                .content(corpoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.nomeFantasia").value("Wonder SA"));
    }

    @Test
    public void updateDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        String corpoJson = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(put("/clientes/{id}", idInexistente)
                .content(corpoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound());
    }
}
