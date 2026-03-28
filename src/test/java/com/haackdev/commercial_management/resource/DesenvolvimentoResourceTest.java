package com.haackdev.commercial_management.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.entity.Desenvolvimento;
import com.haackdev.commercial_management.entity.Pedido;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    private Desenvolvimento desenvolvimento;
    private Pedido pedido;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 99L;
        idDependente = 2L;

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNomeFantasia("Tech Client");

        desenvolvimento = new Desenvolvimento();
        desenvolvimento.setId(idExistente);
        desenvolvimento.setCliente(cliente);

        pedido = new Pedido();
        pedido.setId(10L);
        pedido.setCliente(cliente);

        when(service.findAll()).thenReturn(List.of(desenvolvimento));
        when(service.findById(idExistente)).thenReturn(desenvolvimento);
        when(service.findById(idInexistente)).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any(Desenvolvimento.class))).thenReturn(desenvolvimento);

        when(service.update(eq(idExistente), any(Desenvolvimento.class))).thenReturn(desenvolvimento);
        when(service.update(eq(idInexistente), any(Desenvolvimento.class))).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(idExistente);
        doThrow(ResourceNotFoundException.class).when(service).delete(idInexistente);
        doThrow(DatabaseException.class).when(service).delete(idDependente);

        // Regra do Endpoint Personalizado
        when(service.converterEmPedido(idExistente)).thenReturn(pedido);
        when(service.converterEmPedido(idInexistente)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void findAllDeveRetornarListaEStatusOk() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/desenvolvimentos")
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$[0].id").value(idExistente));
        resultado.andExpect(jsonPath("$[0].cliente.nomeFantasia").value("Tech Client"));
    }

    @Test
    public void findByIdDeveRetornarOkQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/desenvolvimentos/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }

    @Test
    public void findByIdDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/desenvolvimentos/{id}", idInexistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound());
    }

    @Test
    public void insertDeveRetornarCreatedQuandoDadosForemValidos() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(desenvolvimento);

        ResultActions resultado = mockMvc.perform(post("/desenvolvimentos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isCreated());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }

    @Test
    public void deleteDeveRetornarNoContentQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/desenvolvimentos/{id}", idExistente));
        resultado.andExpect(status().isNoContent());
    }

    @Test
    public void updateDeveRetornarOkQuandoIdExistir() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(desenvolvimento);

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

        resultado.andExpect(status().isOk()); // Conforme definido na documentação do endpoint, retorna OK (200) e o objeto convertido.
        resultado.andExpect(jsonPath("$.id").value(10L));
        resultado.andExpect(jsonPath("$.cliente.nomeFantasia").value("Tech Client"));
    }

    @Test
    public void converterEmPedidoDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(post("/desenvolvimentos/{id}/converter-em-pedido", idInexistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound());
    }
}
