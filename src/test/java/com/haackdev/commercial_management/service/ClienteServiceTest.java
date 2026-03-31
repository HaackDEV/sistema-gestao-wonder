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

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private Long idExistente;
    private Long idInexistente;
    private Cliente cliente;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;
        cliente = new Cliente();
        cliente.setId(idExistente);
        cliente.setNomeFantasia("Cliente Teste");
    }

    @Test
    public void findAllDeveRetornarLista() {
        Mockito.when(repository.findAll()).thenReturn(List.of(cliente));
        List<Cliente> result = service.findAll();
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findByIdDeveRetornarClienteQuandoIdExistir() {
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(cliente));
        Cliente result = service.findById(idExistente);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.getId());
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(idInexistente);
        });
    }

    @Test
    public void insertDeveSalvarEGerarIdNull() {
        Mockito.when(repository.save(any())).thenReturn(cliente);
        Cliente result = service.insert(new Cliente());
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).save(any());
    }

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        Mockito.when(repository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(idExistente);
        
        Assertions.assertDoesNotThrow(() -> {
            service.delete(idExistente);
        });
        
        Mockito.verify(repository, Mockito.times(1)).deleteById(idExistente);
    }

    @Test
    public void deleteDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.existsById(idInexistente)).thenReturn(false);
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(idInexistente);
        });
    }

    @Test
    public void deleteDeveLancarDatabaseExceptionQuandoHouverViolacaoDeIntegridade() {
        Mockito.when(repository.existsById(idExistente)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(idExistente);
        
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(idExistente);
        });
    }

    @Test
    public void updateDeveRetornarClienteQuandoIdExistir() {
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(cliente);
        Mockito.when(repository.save(any())).thenReturn(cliente);
        
        Cliente result = service.update(idExistente, cliente);
        
        Assertions.assertNotNull(result);
        Mockito.verify(repository).save(cliente);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(idInexistente, cliente);
        });
    }
}
