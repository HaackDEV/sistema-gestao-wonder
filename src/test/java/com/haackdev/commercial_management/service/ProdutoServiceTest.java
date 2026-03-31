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

/**
 * Testes Unitários da Camada de Serviço para a entidade Produto.
 * Usa Mockito para isolar a lógica de negócio das dependências de banco de dados.
 */
@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    private Long idExistente;
    private Long idInexistente;
    private Produto produto;

    /**
     * Preparação inicial, ocorre ANTES de cada método de teste ser executado.
     * Serve para configurar os cenários comuns de Mock.
     */
    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;
        produto = new Produto();
        produto.setId(idExistente);
        produto.setDescricao("Produto Teste");
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarLista() {
        // ARRANGE
        Mockito.when(repository.findAll()).thenReturn(List.of(produto));
        
        // ACT
        List<Produto> result = service.findAll();
        
        // ASSERT
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void findByIdDeveRetornarProdutoQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(produto));
        
        // ACT
        Produto result = service.findById(idExistente);
        
        // ASSERT
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE INSERÇÃO
    // -------------------------------------------------------------

    @Test
    public void insertDeveSalvarEGerarIdNull() {
        // ARRANGE
        Mockito.when(repository.save(any())).thenReturn(produto);
        
        // ACT
        Produto result = service.insert(new Produto());
        
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
    public void updateDeveRetornarProdutoQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(produto);
        Mockito.when(repository.save(any())).thenReturn(produto);
        
        // ACT
        Produto result = service.update(idExistente, produto);
        
        // ASSERT
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, produto));
    }
}
