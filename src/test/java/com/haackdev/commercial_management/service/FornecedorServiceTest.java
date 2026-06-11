package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.FornecedorRequest;
import com.haackdev.commercial_management.dto.response.FornecedorResponse;
import com.haackdev.commercial_management.entity.Fornecedor;
import com.haackdev.commercial_management.mapper.FornecedorMapper;
import com.haackdev.commercial_management.repository.FornecedorRepository;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Testes Unitários da Camada de Serviço para a entidade Fornecedor.
 * Refatorado para o padrão DTO.
 */
@ExtendWith(MockitoExtension.class)
public class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private FornecedorMapper fornecedorMapper;

    @InjectMocks
    private FornecedorService service;

    private Long idExistente;
    private Long idInexistente;
    private Fornecedor fornecedor;
    private FornecedorRequest requestDTO;
    private FornecedorResponse responseDTO;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;

        fornecedor = new Fornecedor();
        fornecedor.setId(idExistente);
        fornecedor.setNomeFantasia("Fornecedor Teste");

        requestDTO = new FornecedorRequest("Fornecedor Teste");
        responseDTO = new FornecedorResponse(idExistente, "Fornecedor Teste");
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarListaDeFornecedorResponse() {
        // ARRANGE
        Mockito.when(fornecedorRepository.findAll()).thenReturn(List.of(fornecedor));
        Mockito.when(fornecedorMapper.fornecedorToFornecedorResponse(fornecedor)).thenReturn(responseDTO);
        
        // ACT
        List<FornecedorResponse> result = service.findAll();
        
        // ASSERT
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Fornecedor Teste", result.get(0).nomeFantasia());
    }

    @Test
    public void findByIdDeveRetornarFornecedorResponseQuandoIdExistir() {
        // ARRANGE
        Mockito.when(fornecedorRepository.findById(idExistente)).thenReturn(Optional.of(fornecedor));
        Mockito.when(fornecedorMapper.fornecedorToFornecedorResponse(fornecedor)).thenReturn(responseDTO);
        
        // ACT
        FornecedorResponse result = service.findById(idExistente);
        
        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(fornecedorRepository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE INSERÇÃO
    // -------------------------------------------------------------

    @Test
    public void insertDeveSalvarERetornarFornecedorResponse() {
        // ARRANGE
        Mockito.when(fornecedorMapper.requestToFornecedor(any())).thenReturn(fornecedor);
        Mockito.when(fornecedorRepository.save(any())).thenReturn(fornecedor);
        Mockito.when(fornecedorMapper.fornecedorToFornecedorResponse(any())).thenReturn(responseDTO);
        
        // ACT
        FornecedorResponse result = service.insert(requestDTO);
        
        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
        Mockito.verify(fornecedorRepository).save(any());
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE EXCLUSÃO
    // -------------------------------------------------------------

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        // ARRANGE
        Mockito.when(fornecedorRepository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(fornecedorRepository).deleteById(idExistente);
        
        // ACT & ASSERT
        Assertions.assertDoesNotThrow(() -> service.delete(idExistente));
    }

    @Test
    public void deleteDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(fornecedorRepository.existsById(idInexistente)).thenReturn(false);
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarFornecedorResponseQuandoIdExistir() {
        // ARRANGE
        Mockito.when(fornecedorRepository.getReferenceById(idExistente)).thenReturn(fornecedor);
        Mockito.when(fornecedorRepository.save(any())).thenReturn(fornecedor);
        Mockito.when(fornecedorMapper.fornecedorToFornecedorResponse(any())).thenReturn(responseDTO);
        
        // ACT
        FornecedorResponse result = service.update(idExistente, requestDTO);
        
        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(fornecedorRepository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, requestDTO));
    }
}
