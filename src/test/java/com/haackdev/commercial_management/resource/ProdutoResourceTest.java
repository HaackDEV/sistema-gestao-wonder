package com.haackdev.commercial_management.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haackdev.commercial_management.dto.request.ProdutoRequest;
import com.haackdev.commercial_management.dto.response.ProdutoResponse;
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
    private ProdutoRequest requestDTO;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 99L;
        idDependente = 2L;

        ProdutoResponse responseDTO = new ProdutoResponse(
                idExistente,
                "PROD-01",
                "Camiseta Wonder",
                "Vestuário",
                "Azul",
                "Algodão",
                BigDecimal.valueOf(40.0),
                BigDecimal.valueOf(89.90),
                1L,
                "Fornecedor SA"
        );

        requestDTO = new ProdutoRequest(
                1L,
                "PROD-01",
                "Camiseta Wonder",
                "Vestuário",
                "Azul",
                "Algodão",
                BigDecimal.valueOf(40.0),
                BigDecimal.valueOf(89.90)
        );

        // FIND ALL
        when(service.findAll()).thenReturn(List.of(responseDTO));

        // FIND BY ID
        when(service.findById(idExistente)).thenReturn(responseDTO);
        when(service.findById(idInexistente)).thenThrow(ResourceNotFoundException.class);

        // INSERT
        when(service.insert(any(ProdutoRequest.class))).thenReturn(responseDTO);

        // UPDATE
        when(service.update(eq(idExistente), any(ProdutoRequest.class))).thenReturn(responseDTO);
        when(service.update(eq(idInexistente), any(ProdutoRequest.class))).thenThrow(ResourceNotFoundException.class);

        // DELETE
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
        resultado.andExpect(jsonPath("$[0].fornecedorNomeFantasia").value("Fornecedor SA"));
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
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(post("/produtos")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isCreated());
        resultado.andExpect(header().exists("Location"));
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
        String jsonBody = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(put("/produtos/{id}", idExistente)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.descricao").value("Camiseta Wonder"));
    }
}
