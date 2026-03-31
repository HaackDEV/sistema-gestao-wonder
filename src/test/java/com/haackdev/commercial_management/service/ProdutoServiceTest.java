package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.Produto;
import com.haackdev.commercial_management.repository.ProdutoRepository;
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
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    private Long idExistente;
    private Long idInexistente;
    private Produto produto;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;
        produto = new Produto();
        produto.setId(idExistente);
        produto.setDescricao("Produto Teste");
    }

    @Test
    public void findAllDeveRetornarLista() {
        Mockito.when(repository.findAll()).thenReturn(List.of(produto));
        List<Produto> result = service.findAll();
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void findByIdDeveRetornarProdutoQuandoIdExistir() {
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(produto));
        Produto result = service.findById(idExistente);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }

    @Test
    public void insertDeveSalvarEGerarIdNull() {
        Mockito.when(repository.save(any())).thenReturn(produto);
        Produto result = service.insert(new Produto());
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
    public void updateDeveRetornarProdutoQuandoIdExistir() {
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(produto);
        Mockito.when(repository.save(any())).thenReturn(produto);
        
        Produto result = service.update(idExistente, produto);
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, produto));
    }
}
