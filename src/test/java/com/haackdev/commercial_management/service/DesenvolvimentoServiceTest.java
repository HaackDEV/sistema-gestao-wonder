package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.DesenvolvimentoRequest;
import com.haackdev.commercial_management.dto.request.PedidoRequest;
import com.haackdev.commercial_management.dto.response.DesenvolvimentoResponse;
import com.haackdev.commercial_management.dto.response.PedidoResponse;
import com.haackdev.commercial_management.entity.*;
import com.haackdev.commercial_management.entity.enums.StatusDesenvolvimento;
import com.haackdev.commercial_management.mapper.DesenvolvimentoMapper;
import com.haackdev.commercial_management.repository.ClienteRepository;
import com.haackdev.commercial_management.repository.DesenvolvimentoRepository;
import com.haackdev.commercial_management.repository.ProdutoRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;

/**
 * Testes Unitários da Camada de Serviço para a entidade Desenvolvimento.
 * Agora refatorado para utilizar DTOs (Request e Response) e os repositórios auxiliares.
 */
@ExtendWith(MockitoExtension.class)
public class DesenvolvimentoServiceTest {

    // 1. Injeção das dependências EXATAS que o construtor do Service pede
    @Mock
    private DesenvolvimentoRepository desenvolvimentoRepository;

    @Mock
    private DesenvolvimentoMapper desenvolvimentoMapper;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private DesenvolvimentoService service;

    private Long idExistente;
    private Long idInexistente;
    private Desenvolvimento desenvolvimento;
    private Cliente cliente;
    private Produto produto;
    
    private DesenvolvimentoRequest desenvolvimentoRequest;
    private DesenvolvimentoResponse desenvolvimentoResponse;
    private PedidoResponse pedidoResponse;

    /**
     * Preparação inicial, ocorre ANTES de cada método de teste ser executado.
     * Define o comportamento básico dos mocks e instâncias de teste.
     */
    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 99L;

        cliente = new Cliente();
        cliente.setId(10L);
        cliente.setNomeFantasia("Cliente Teste");

        produto = new Produto();
        produto.setId(20L);
        produto.setCodigoProduto("PROD-01");
        produto.setDescricao("Produto Teste");

        // Entidade de Banco
        desenvolvimento = new Desenvolvimento();
        desenvolvimento.setId(idExistente);
        desenvolvimento.setCliente(cliente);
        desenvolvimento.setProduto(produto);
        desenvolvimento.setTipo("Amostra");
        desenvolvimento.setDataSolicitacao(LocalDate.now());
        desenvolvimento.setStatus(StatusDesenvolvimento.APROVADO);
        desenvolvimento.setVirouPedido(false);
        desenvolvimento.setValorConvertido(new BigDecimal("100.00"));

        // DTO de Entrada
        desenvolvimentoRequest = new DesenvolvimentoRequest(
                10L, 20L, "Amostra", LocalDate.now(), StatusDesenvolvimento.APROVADO, null, false, new BigDecimal("100.00"), null
        );

        // DTO de Saída
        desenvolvimentoResponse = new DesenvolvimentoResponse(
                idExistente, 10L, "Cliente Teste", 20L, "PROD-01", "Produto Teste", "Amostra", LocalDate.now(), StatusDesenvolvimento.APROVADO, null, false, new BigDecimal("100.00"), null
        );

        // DTO de Saída do Pedido (quando convertido)
        pedidoResponse = new PedidoResponse(
                5L, 10L, "Cliente Teste", LocalDate.now(), new BigDecimal("100.00"), "Conversão de Desenvolvimento", "1x", List.of()
        );
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarListaDeDesenvolvimentoResponse() {
        // ARRANGE
        Mockito.when(desenvolvimentoRepository.findAll()).thenReturn(List.of(desenvolvimento));
        Mockito.when(desenvolvimentoMapper.desenvolvimentoToDesenvolvimentoResponse(desenvolvimento)).thenReturn(desenvolvimentoResponse);

        // ACT
        List<DesenvolvimentoResponse> result = service.findAll();

        // ASSERT
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(idExistente, result.get(0).id());
        Assertions.assertEquals("Cliente Teste", result.get(0).clienteNomeFantasia());
    }

    @Test
    public void findByIdDeveRetornarDesenvolvimentoResponseQuandoIdExistir() {
        // ARRANGE
        Mockito.when(desenvolvimentoRepository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        Mockito.when(desenvolvimentoMapper.desenvolvimentoToDesenvolvimentoResponse(desenvolvimento)).thenReturn(desenvolvimentoResponse);

        // ACT
        DesenvolvimentoResponse result = service.findById(idExistente);

        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(desenvolvimentoRepository.findById(idInexistente)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE INSERÇÃO
    // -------------------------------------------------------------

    @Test
    public void insertDeveSalvarERetornarDesenvolvimentoResponseQuandoDadosValidos() {
        // ARRANGE: O Mapper devolve uma entidade vazia, e os repositórios auxiliadores validam as chaves
        Mockito.when(desenvolvimentoMapper.requestToDesenvolvimento(any())).thenReturn(new Desenvolvimento());
        
        // Simulando que Cliente e Produto existem no banco
        Mockito.when(clienteRepository.existsById(10L)).thenReturn(true);
        Mockito.when(produtoRepository.existsById(20L)).thenReturn(true);
        Mockito.when(clienteRepository.getReferenceById(10L)).thenReturn(cliente);
        Mockito.when(produtoRepository.getReferenceById(20L)).thenReturn(produto);
        
        Mockito.when(desenvolvimentoRepository.save(any())).thenReturn(desenvolvimento);
        Mockito.when(desenvolvimentoMapper.desenvolvimentoToDesenvolvimentoResponse(desenvolvimento)).thenReturn(desenvolvimentoResponse);

        // ACT
        DesenvolvimentoResponse result = service.insert(desenvolvimentoRequest);

        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
        Mockito.verify(desenvolvimentoRepository).save(any());
    }

    @Test
    public void insertDeveLancarResourceNotFoundExceptionQuandoClienteNaoExistir() {
        Mockito.when(desenvolvimentoMapper.requestToDesenvolvimento(any())).thenReturn(new Desenvolvimento());
        Mockito.when(clienteRepository.existsById(10L)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(desenvolvimentoRequest));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarDesenvolvimentoResponseQuandoIdExistir() {
        // ARRANGE
        Mockito.when(desenvolvimentoRepository.getReferenceById(idExistente)).thenReturn(desenvolvimento);
        
        Mockito.when(clienteRepository.existsById(10L)).thenReturn(true);
        Mockito.when(produtoRepository.existsById(20L)).thenReturn(true);
        Mockito.when(clienteRepository.getReferenceById(10L)).thenReturn(cliente);
        Mockito.when(produtoRepository.getReferenceById(20L)).thenReturn(produto);
        
        Mockito.when(desenvolvimentoRepository.save(any())).thenReturn(desenvolvimento);
        Mockito.when(desenvolvimentoMapper.desenvolvimentoToDesenvolvimentoResponse(desenvolvimento)).thenReturn(desenvolvimentoResponse);

        // ACT
        DesenvolvimentoResponse result = service.update(idExistente, desenvolvimentoRequest);

        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
        Mockito.verify(desenvolvimentoRepository).save(desenvolvimento);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(desenvolvimentoRepository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, desenvolvimentoRequest));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE EXCLUSÃO
    // -------------------------------------------------------------

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        Mockito.when(desenvolvimentoRepository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(desenvolvimentoRepository).deleteById(idExistente);

        Assertions.assertDoesNotThrow(() -> service.delete(idExistente));
        Mockito.verify(desenvolvimentoRepository).deleteById(idExistente);
    }

    @Test
    public void deleteDeveLancarDatabaseExceptionQuandoHouverViolacaoDeIntegridade() {
        Mockito.when(desenvolvimentoRepository.existsById(idExistente)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(desenvolvimentoRepository).deleteById(idExistente);

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(idExistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONVERSÃO (DESENVOLVIMENTO -> PEDIDO)
    // -------------------------------------------------------------

    @Test
    public void converterEmPedidoDeveRetornarPedidoResponseQuandoSucesso() {
        // ARRANGE
        Mockito.when(desenvolvimentoRepository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        Mockito.when(pedidoService.insert(any(PedidoRequest.class))).thenReturn(pedidoResponse);
        
        // ACT
        PedidoResponse result = service.converterEmPedido(idExistente);
        
        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertEquals(5L, result.id());
        Assertions.assertTrue(desenvolvimento.getVirouPedido()); // Garante que a entidade foi alterada em memória
        Assertions.assertNotNull(desenvolvimento.getDataConversao());
        
        Mockito.verify(desenvolvimentoRepository).save(desenvolvimento); // Garante que o desenvolvimento atualizado foi salvo
        Mockito.verify(pedidoService).insert(any(PedidoRequest.class)); // Garante que chamou o service de pedido enviando o DTO
    }

    @Test
    public void converterEmPedidoDeveLancarDatabaseExceptionQuandoJaConvertido() {
        desenvolvimento.setVirouPedido(true);
        Mockito.when(desenvolvimentoRepository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }

    @Test
    public void converterEmPedidoDeveLancarDatabaseExceptionQuandoNaoAprovado() {
        desenvolvimento.setStatus(StatusDesenvolvimento.EM_ANALISE);
        Mockito.when(desenvolvimentoRepository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }

    @Test
    public void converterEmPedidoDeveLancarDatabaseExceptionQuandoValorConvertidoForNulo() {
        desenvolvimento.setValorConvertido(null);
        Mockito.when(desenvolvimentoRepository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }
}
