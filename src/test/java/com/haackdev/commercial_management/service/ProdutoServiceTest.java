package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.ProdutoRequest;
import com.haackdev.commercial_management.dto.response.ProdutoResponse;
import com.haackdev.commercial_management.entity.Fornecedor;
import com.haackdev.commercial_management.entity.Produto;
import com.haackdev.commercial_management.mapper.ProdutoMapper;
import com.haackdev.commercial_management.repository.FornecedorRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Testes Unitários da Camada de Serviço para a entidade Produto.
 * Refatorado para o padrão DTO.
 */
@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @InjectMocks
    private ProdutoService service;

    private Long idExistente;
    private Long idInexistente;
    private Produto produto;
    private Fornecedor fornecedor;
    private ProdutoRequest requestDTO;
    private ProdutoResponse responseDTO;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 99L;

        fornecedor = new Fornecedor();
        fornecedor.setId(10L);
        fornecedor.setNomeFantasia("Fornecedor Teste");

        produto = new Produto();
        produto.setId(idExistente);
        produto.setDescricao("Produto Teste");
        produto.setFornecedor(fornecedor);

        requestDTO = new ProdutoRequest(
                10L, "PROD-01", "Produto Teste", "Amostra", "Azul", "Algodão", new BigDecimal("10.00"), new BigDecimal("20.00")
        );

        responseDTO = new ProdutoResponse(
                idExistente, "PROD-01", "Produto Teste", "Amostra", "Azul", "Algodão", new BigDecimal("10.00"), new BigDecimal("20.00"), 10L, "Fornecedor Teste"
        );
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarListaDeProdutoResponse() {
        Mockito.when(produtoRepository.findAll()).thenReturn(List.of(produto));
        Mockito.when(produtoMapper.ProdutoToProdutoResponse(produto)).thenReturn(responseDTO);
        
        List<ProdutoResponse> result = service.findAll();
        
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(idExistente, result.get(0).id());
    }

    @Test
    public void findByIdDeveRetornarProdutoResponseQuandoIdExistir() {
        Mockito.when(produtoRepository.findById(idExistente)).thenReturn(Optional.of(produto));
        Mockito.when(produtoMapper.ProdutoToProdutoResponse(produto)).thenReturn(responseDTO);
        
        ProdutoResponse result = service.findById(idExistente);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(produtoRepository.findById(idInexistente)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE INSERÇÃO
    // -------------------------------------------------------------

    @Test
    public void insertDeveSalvarERetornarProdutoResponse() {
        Mockito.when(produtoMapper.requestToProduto(any())).thenReturn(produto);
        Mockito.when(fornecedorRepository.existsById(10L)).thenReturn(true);
        Mockito.when(fornecedorRepository.getReferenceById(10L)).thenReturn(fornecedor);
        Mockito.when(produtoRepository.save(any())).thenReturn(produto);
        Mockito.when(produtoMapper.ProdutoToProdutoResponse(any())).thenReturn(responseDTO);
        
        ProdutoResponse result = service.insert(requestDTO);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
        Mockito.verify(produtoRepository).save(any());
    }

    @Test
    public void insertDeveLancarResourceNotFoundExceptionQuandoFornecedorNaoExistir() {
        Mockito.when(produtoMapper.requestToProduto(any())).thenReturn(produto);
        Mockito.when(fornecedorRepository.existsById(10L)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(requestDTO));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE EXCLUSÃO
    // -------------------------------------------------------------

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        Mockito.when(produtoRepository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(produtoRepository).deleteById(idExistente);
        
        Assertions.assertDoesNotThrow(() -> service.delete(idExistente));
    }

    @Test
    public void deleteDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(produtoRepository.existsById(idInexistente)).thenReturn(false);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarProdutoResponseQuandoIdExistir() {
        Mockito.when(produtoRepository.getReferenceById(idExistente)).thenReturn(produto);
        Mockito.when(fornecedorRepository.existsById(10L)).thenReturn(true);
        Mockito.when(fornecedorRepository.getReferenceById(10L)).thenReturn(fornecedor);
        Mockito.when(produtoRepository.save(any())).thenReturn(produto);
        Mockito.when(produtoMapper.ProdutoToProdutoResponse(any())).thenReturn(responseDTO);
        
        ProdutoResponse result = service.update(idExistente, requestDTO);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(produtoRepository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, requestDTO));
    }
}
