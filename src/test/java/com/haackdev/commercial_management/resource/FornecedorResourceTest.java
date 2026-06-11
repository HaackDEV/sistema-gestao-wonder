package com.haackdev.commercial_management.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haackdev.commercial_management.dto.request.FornecedorRequest;
import com.haackdev.commercial_management.dto.response.FornecedorResponse;
import com.haackdev.commercial_management.entity.Fornecedor;
import com.haackdev.commercial_management.service.FornecedorService;
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
 * Testes de Integração da Camada Web (Controller) para Fornecedor.
 */
@WebMvcTest(FornecedorResource.class)
public class FornecedorResourceTest {

    @Autowired
    private MockMvc mockMvc; // Emulador de requisições HTTP

    @MockitoBean
    private FornecedorService service; // Mock da camada de inteligência/banco

    private ObjectMapper objectMapper = new ObjectMapper();

    private Long idExistente;
    private Long idInexistente;
    private Long idDependente;
    private FornecedorRequest requestDTO;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 99L;
        idDependente = 2L;

        FornecedorResponse responseDTO = new FornecedorResponse(1L, "Tech Fornecedora");

        requestDTO = new FornecedorRequest("Tech Fornecedora");

        // FIND ALL
        when(service.findAll()).thenReturn(List.of(responseDTO));

        // FIND BY ID
        when(service.findById(idExistente)).thenReturn(responseDTO);
        when(service.findById(idInexistente)).thenThrow(ResourceNotFoundException.class);

        // INSERT
        when(service.insert(any(FornecedorRequest.class))).thenReturn(responseDTO);

        // UPDATE
        when(service.update(eq(idExistente), any(FornecedorRequest.class))).thenReturn(responseDTO);
        when(service.update(eq(idInexistente), any(FornecedorRequest.class))).thenThrow(ResourceNotFoundException.class);

        // DELETE
        doNothing().when(service).delete(idExistente);
        doThrow(ResourceNotFoundException.class).when(service).delete(idInexistente);
        doThrow(DatabaseException.class).when(service).delete(idDependente);
    }

    @Test
    public void findAllDeveRetornarListaDeFornecedoresEStatusOk() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/fornecedores")
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$[0].id").value(idExistente));
        resultado.andExpect(jsonPath("$[0].nomeFantasia").value("Tech Fornecedora"));
    }

    @Test
    public void findByIdDeveRetornarFornecedorEStatusOkQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/fornecedores/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.nomeFantasia").value("Tech Fornecedora"));
    }

    @Test
    public void findByIdDeveRetornarNotFoundQuandoIdNaoExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(get("/fornecedores/{id}", idInexistente)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isNotFound());
    }

    @Test
    public void insertDeveRetornarStatusCreatedEFornecedor() throws Exception {
        String corpoJson = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(post("/fornecedores")
                .content(corpoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isCreated());
        resultado.andExpect(header().exists("Location"));
        resultado.andExpect(jsonPath("$.id").value(idExistente));
    }

    @Test
    public void deleteDeveRetornarNoContentQuandoIdExistir() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/fornecedores/{id}", idExistente));
        resultado.andExpect(status().isNoContent());
    }

    @Test
    public void deleteDeveRetornarBadRequestQuandoFornecedorTiverDependencias() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/fornecedores/{id}", idDependente));
        resultado.andExpect(status().isBadRequest());
    }

    @Test
    public void updateDeveRetornarStatusOkQuandoIdExistir() throws Exception {
        String corpoJson = objectMapper.writeValueAsString(requestDTO);

        ResultActions resultado = mockMvc.perform(put("/fornecedores/{id}", idExistente)
                .content(corpoJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.id").value(idExistente));
        resultado.andExpect(jsonPath("$.nomeFantasia").value("Tech Fornecedora"));
    }
}
