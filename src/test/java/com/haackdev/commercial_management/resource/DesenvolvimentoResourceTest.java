package com.haackdev.commercial_management.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.haackdev.commercial_management.dto.request.DesenvolvimentoRequest;
import com.haackdev.commercial_management.dto.response.DesenvolvimentoResponse;
import com.haackdev.commercial_management.dto.response.PedidoResponse;
import com.haackdev.commercial_management.entity.enums.StatusDesenvolvimento;
import com.haackdev.commercial_management.service.DesenvolvimentoService;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração da Camada Web (Controller) para Desenvolvimento.
 */
@WebMvcTest(DesenvolvimentoResource.class)
public class DesenvolvimentoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DesenvolvimentoService service;

    private ObjectMapper objectMapper;

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    
    private DesenvolvimentoRequest requestDTO;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Necessário para LocalDate

        idExistente = 1L;
        idInexistente = 99L;
        idDependente = 2L;

        DesenvolvimentoResponse responseDTO = new DesenvolvimentoResponse(
                idExistente,
                1L,
                "Tech Client",
                4L,
                "PROD-01",
                "Produto Teste",
                "Amostra",
                LocalDate.now(),
                StatusDesenvolvimento.EM_ANALISE,
                null,
                false,
                null,
                null
        );

        requestDTO = new DesenvolvimentoRequest(
                1L,
                4L,
                "Amostra",
                LocalDate.now(),
                StatusDesenvolvimento.EM_ANALISE,
                null,
                false,
                null,
                null
        );

        PedidoResponse pedidoResponse = new PedidoResponse(
                10L, 1L, "Tech Client", LocalDate.now(), null, null, null, List.of()
        );

        when(service.findAll()).thenReturn(List.of(responseDTO));
        when(service.findById(idExistente)).thenReturn(responseDTO);
        when(service.findById(idInexistente)).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any(DesenvolvimentoRequest.class))).thenReturn(responseDTO);

        when(service.update(eq(idExistente), any(DesenvolvimentoRequest.class))).thenReturn(responseDTO);
        when(service.update(eq(idInexistente), any(DesenvolvimentoRequest.class))).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(idExistente);
        doThrow(ResourceNotFoundException.class).when(service).delete(idInexistente);
        doThrow(DatabaseException.class).when(service).delete(idDependente);

        // Regra do Endpoint Personalizado
        when(service.converterEmPedido(idExistente)).thenReturn(pedidoResponse);
        when(service.converterEmPedido(idInexistente)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void findAllDeveRetornarListaEStatusOk() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/desenvolvimentos")
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$[0].id").value(idExistente));
        resultado.andExpect(jsonPath("$[0].clienteNomeFantasia").value("Tech Client"));
    }

    @Test
    public void findByIdDeveRetornarOkQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/desenvolvimentos/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.clienteNomeFantasia").value("Tech Client"));
    }

    @Test
    public void findByIdDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/desenvolvimentos/{id}", idInexistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound());
    }

    @Test
    public void insertDeveRetornarCreatedQuandoDadosForemValidos() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(post("/desenvolvimentos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isCreated());
        resultado.andExpect(header().exists("Location"));
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }

    @Test
    public void deleteDeveRetornarNoContentQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/desenvolvimentos/{id}", idExistente));
        resultado.andExpect(status().isNoContent());
    }

    @Test
    public void updateDeveRetornarOkQuandoIdExistir() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(put("/desenvolvimentos/{id}", idExistente)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }

    // --- TESTES DO ENDPOINT PERSONALIZADO ---

    @Test
    public void converterEmPedidoDeveRetornarPedidoEStatusOkQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(post("/desenvolvimentos/{id}/converter-em-pedido", idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk()); 
        resultado.andExpect(jsonPath("$.id").value(10L)); 
    }

    @Test
    public void converterEmPedidoDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(post("/desenvolvimentos/{id}/converter-em-pedido", idInexistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound());
    }
}
