package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.Fornecedor;
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

/**
 * Testes Unitários da Camada de Serviço para a entidade Fornecedor.
 * Usa Mockito para isolar a lógica de negócio das dependências de banco de dados.
 */
@ExtendWith(MockitoExtension.class)
public class FornecedorServiceTest {

    @Mock
    private FornecedorRepository repository;

    @InjectMocks
    private FornecedorService service;

    private Long idExistente;
    private Long idInexistente;
    private Fornecedor fornecedor;

    /**
     * Preparação inicial, ocorre ANTES de cada método de teste ser executado.
     * Serve para configurar os cenários comuns de Mock.
     */
    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;
        fornecedor = new Fornecedor();
        fornecedor.setId(idExistente);
        fornecedor.setNomeFantasia("Fornecedor Teste");
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarLista() {
        // ARRANGE
        Mockito.when(repository.findAll()).thenReturn(List.of(fornecedor));
        
        // ACT
        List<Fornecedor> result = service.findAll();
        
        // ASSERT
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void findByIdDeveRetornarFornecedorQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(fornecedor));
        
        // ACT
        Fornecedor result = service.findById(idExistente);
        
        // ASSERT
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(idInexistente);
        });
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE INSERÇÃO
    // -------------------------------------------------------------

    @Test
    public void insertDeveSalvarEGerarIdNull() {
        // ARRANGE
        Mockito.when(repository.save(any())).thenReturn(fornecedor);
        
        // ACT
        Fornecedor result = service.insert(new Fornecedor());
        
        // ASSERT
        Assertions.assertNotNull(result);
        Mockito.verify(repository).save(any());
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE EXCLUSÃO
    // -------------------------------------------------------------

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(idExistente);
        
        // ACT & ASSERT
        Assertions.assertDoesNotThrow(() -> service.delete(idExistente));
    }

    @Test
    public void deleteDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.existsById(idInexistente)).thenReturn(false);
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarFornecedorQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(fornecedor);
        Mockito.when(repository.save(any())).thenReturn(fornecedor);
        
        // ACT
        Fornecedor result = service.update(idExistente, fornecedor);
        
        // ASSERT
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, fornecedor));
    }
}
