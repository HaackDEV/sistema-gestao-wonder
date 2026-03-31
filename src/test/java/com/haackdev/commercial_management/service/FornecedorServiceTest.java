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

@ExtendWith(MockitoExtension.class)
public class FornecedorServiceTest {

    @Mock
    private FornecedorRepository repository;

    @InjectMocks
    private FornecedorService service;

    private Long idExistente;
    private Long idInexistente;
    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;
        fornecedor = new Fornecedor();
        fornecedor.setId(idExistente);
        fornecedor.setNomeFantasia("Fornecedor Teste");
    }

    @Test
    public void findAllDeveRetornarLista() {
        Mockito.when(repository.findAll()).thenReturn(List.of(fornecedor));
        List<Fornecedor> result = service.findAll();
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void findByIdDeveRetornarFornecedorQuandoIdExistir() {
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(fornecedor));
        Fornecedor result = service.findById(idExistente);
        Assertions.assertNotNull(result);
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
        Mockito.when(repository.save(any())).thenReturn(fornecedor);
        Fornecedor result = service.insert(new Fornecedor());
        Assertions.assertNotNull(result);
        Mockito.verify(repository).save(any());
    }

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        Mockito.when(repository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(idExistente);
        Assertions.assertDoesNotThrow(() -> service.delete(idExistente));
    }

    @Test
    public void deleteDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.existsById(idInexistente)).thenReturn(false);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idInexistente));
    }

    @Test
    public void updateDeveRetornarFornecedorQuandoIdExistir() {
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(fornecedor);
        Mockito.when(repository.save(any())).thenReturn(fornecedor);
        
        Fornecedor result = service.update(idExistente, fornecedor);
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, fornecedor));
    }
}
