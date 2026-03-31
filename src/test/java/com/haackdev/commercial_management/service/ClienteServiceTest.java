package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.repository.ClienteRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

/**
 * Testes Unitários da Camada de Serviço para a entidade Cliente.
 * Usa Mockito para isolar a lógica de negócio das dependências de banco de dados.
 */
@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private Long idExistente;
    private Long idInexistente;
    private Cliente cliente;

    /**
     * Preparação inicial, ocorre ANTES de cada método de teste ser executado.
     * Serve para configurar os cenários comuns de Mock.
     */
    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;
        cliente = new Cliente();
        cliente.setId(idExistente);
        cliente.setNomeFantasia("Cliente Teste");
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarLista() {
        // ARRANGE
        Mockito.when(repository.findAll()).thenReturn(List.of(cliente));
        
        // ACT
        List<Cliente> result = service.findAll();
        
        // ASSERT
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findByIdDeveRetornarClienteQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(cliente));
        
        // ACT
        Cliente result = service.findById(idExistente);
        
        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.getId());
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
        Mockito.when(repository.save(any())).thenReturn(cliente);
        
        // ACT
        Cliente result = service.insert(new Cliente());
        
        // ASSERT
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).save(any());
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
        Assertions.assertDoesNotThrow(() -> {
            service.delete(idExistente);
        });
        
        Mockito.verify(repository, Mockito.times(1)).deleteById(idExistente);
    }

    @Test
    public void deleteDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.existsById(idInexistente)).thenReturn(false);
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(idInexistente);
        });
    }

    @Test
    public void deleteDeveLancarDatabaseExceptionQuandoHouverViolacaoDeIntegridade() {
        // ARRANGE
        Mockito.when(repository.existsById(idExistente)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idExistente);
        
        // ACT & ASSERT
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(idExistente);
        });
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarClienteQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(cliente);
        Mockito.when(repository.save(any())).thenReturn(cliente);
        
        // ACT
        Cliente result = service.update(idExistente, cliente);
        
        // ASSERT
        Assertions.assertNotNull(result);
        Mockito.verify(repository).save(cliente);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(idInexistente, cliente);
        });
    }
}
