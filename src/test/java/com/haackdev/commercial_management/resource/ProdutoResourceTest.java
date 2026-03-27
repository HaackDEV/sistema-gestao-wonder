package com.haackdev.commercial_management.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haackdev.commercial_management.entity.Produto;
import com.haackdev.commercial_management.service.ProdutoService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração da Camada Web (Controller) para Produto.
 */
@WebMvcTest(ProdutoResource.class)
public class ProdutoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService service;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    private Produto produto;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 99L;
        idDependente = 2L;

        produto = new Produto();
        produto.setId(idExistente);
        produto.setDescricao("Camiseta Wonder");
        produto.setValorVenda(BigDecimal.valueOf(89.90));

        when(service.findAll()).thenReturn(List.of(produto));
        when(service.findById(idExistente)).thenReturn(produto);
        when(service.findById(idInexistente)).thenThrow(ResourceNotFoundException.class);

        when(service.insert(any(Produto.class))).thenReturn(produto);

        when(service.update(eq(idExistente), any(Produto.class))).thenReturn(produto);
        when(service.update(eq(idInexistente), any(Produto.class))).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(idExistente);
        doThrow(ResourceNotFoundException.class).when(service).delete(idInexistente);
        doThrow(DatabaseException.class).when(service).delete(idDependente);
    }

    @Test
    public void findAllDeveRetornarListaEStatusOk() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/produtos")
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$[0].id").value(idExistente));
        resultado.andExpect(jsonPath("$[0].descricao").value("Camiseta Wonder"));
    }

    @Test
    public void findByIdDeveRetornarProdutoEStatusOkQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/produtos/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.descricao").value("Camiseta Wonder"));
    }

    @Test
    public void findByIdDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/produtos/{id}", idInexistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound());
    }

    @Test
    public void insertDeveRetornarCreatedEProduto() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(produto);

        ResultActions resultado = mockMvc.perform(post("/produtos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isCreated());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }

    @Test
    public void deleteDeveRetornarNoContentQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/produtos/{id}", idExistente));
        resultado.andExpect(status().isNoContent());
    }

    @Test
    public void deleteDeveRestornarBadRequestParaDatabaseException() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/produtos/{id}", idDependente));
        resultado.andExpect(status().isBadRequest());
    }

    @Test
    public void updateDeveRetornarOkQuandoIdExistir() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(produto);

        ResultActions resultado = mockMvc.perform(put("/produtos/{id}", idExistente)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.descricao").value("Camiseta Wonder"));
    }
}
