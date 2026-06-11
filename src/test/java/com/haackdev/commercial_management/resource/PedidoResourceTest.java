package com.haackdev.commercial_management.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.haackdev.commercial_management.dto.request.ItemPedidoRequest;
import com.haackdev.commercial_management.dto.request.PedidoRequest;
import com.haackdev.commercial_management.dto.response.ItemPedidoResponse;
import com.haackdev.commercial_management.dto.response.PedidoResponse;
import com.haackdev.commercial_management.service.PedidoService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração da Camada Web (Controller) para Pedido.
 */
@WebMvcTest(PedidoResource.class)
public class PedidoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService service;

    private ObjectMapper objectMapper;

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    
    private PedidoRequest requestDTO;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Necessário para datas

        idExistente = 1L;
        idInexistente = 99L;
        idDependente = 2L;

        ItemPedidoResponse itemResponse = new ItemPedidoResponse(1L, 5L, "Produto Teste", 2, BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0));
        PedidoResponse responseDTO = new PedidoResponse(
                idExistente,
                1L,
                "Tech Client Order",
                LocalDate.now(),
                BigDecimal.valueOf(20.0),
                "Boleto",
                "1x",
                List.of(itemResponse)
        );

        ItemPedidoRequest itemRequest = new ItemPedidoRequest(5L, 2, BigDecimal.valueOf(10.0));
        requestDTO = new PedidoRequest(
                1L,
                LocalDate.now(),
                "Boleto",
                "1x",
                List.of(itemRequest)
        );

        when(service.findAll()).thenReturn(List.of(responseDTO));
        when(service.findById(idExistente)).thenReturn(responseDTO);
        when(service.findById(idInexistente)).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any(PedidoRequest.class))).thenReturn(responseDTO);

        when(service.update(eq(idExistente), any(PedidoRequest.class))).thenReturn(responseDTO);
        when(service.update(eq(idInexistente), any(PedidoRequest.class))).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(idExistente);
        doThrow(ResourceNotFoundException.class).when(service).delete(idInexistente);
        doThrow(DatabaseException.class).when(service).delete(idDependente);
    }

    @Test
    public void findAllDeveRetornarListaEStatusOk() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/pedidos")
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$[0].id").value(idExistente));
        resultado.andExpect(jsonPath("$[0].clienteNomeFantasia").value("Tech Client Order"));
    }

    @Test
    public void findByIdDeveRetornarOkQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/pedidos/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }

    @Test
    public void findByIdDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/pedidos/{id}", idInexistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound());
    }

    @Test
    public void insertDeveRetornarCreatedQuandoDadosForemValidos() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(post("/pedidos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isCreated());
        resultado.andExpect(header().exists("Location"));
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }

    @Test
    public void deleteDeveRetornarNoContentQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/pedidos/{id}", idExistente));
        resultado.andExpect(status().isNoContent());
    }

    @Test
    public void updateDeveRetornarOkQuandoIdExistir() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(put("/pedidos/{id}", idExistente)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }
}
