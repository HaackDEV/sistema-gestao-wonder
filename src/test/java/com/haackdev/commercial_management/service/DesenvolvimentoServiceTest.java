package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.*;
import com.haackdev.commercial_management.entity.enums.StatusDesenvolvimento;
import com.haackdev.commercial_management.repository.DesenvolvimentoRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

/**
 * Testes Unitários da Camada de Serviço para a entidade Desenvolvimento.
 * Focado na lógica de conversão de Desenvolvimento para Pedido.
 */
@ExtendWith(MockitoExtension.class)
public class DesenvolvimentoServiceTest {

    @Mock
    private DesenvolvimentoRepository repository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private DesenvolvimentoService service;

    private Long idExistente;
    private Long idInexistente;
    private Desenvolvimento desenvolvimento;
    private Cliente cliente;
    private Produto produto;

    /**
     * Preparação inicial, ocorre ANTES de cada método de teste ser executado.
     * Define o comportamento básico dos mocks e instâncias de teste.
     */
    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;

        cliente = new Cliente();
        cliente.setId(10L);
        cliente.setNomeFantasia("Cliente Teste");

        produto = new Produto();
        produto.setId(20L);
        produto.setDescricao("Produto Teste");

        desenvolvimento = new Desenvolvimento();
        desenvolvimento.setId(idExistente);
        desenvolvimento.setCliente(cliente);
        desenvolvimento.setProduto(produto);
        desenvolvimento.setStatus(StatusDesenvolvimento.APROVADO);
        desenvolvimento.setVirouPedido(false);
        desenvolvimento.setValorConvertido(new BigDecimal("100.00"));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONVERSÃO (DESENVOLVIMENTO -> PEDIDO)
    // -------------------------------------------------------------

    @Test
    public void converterEmPedidoDeveRetornarPedidoQuandoSucesso() {
        // ARRANGE (Configura o mock para retornar o desenvolvimento e simular a inserção do pedido)
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        Mockito.when(pedidoService.insert(any())).thenAnswer(invocation -> invocation.getArgument(0));
        
        // ACT (Executa a conversão)
        Pedido result = service.converterEmPedido(idExistente);
        
        // ASSERT (Verifica se os dados do pedido gerado estão corretos e se o desenvolvimento foi atualizado)
        Assertions.assertNotNull(result);
        Assertions.assertEquals(cliente, result.getCliente());
        Assertions.assertEquals(1, result.getItens().size());
        Assertions.assertEquals(produto, result.getItens().get(0).getProduto());
        Assertions.assertEquals(new BigDecimal("100.00"), result.getItens().get(0).getValorUnitario());
        Assertions.assertTrue(desenvolvimento.getVirouPedido());
        Assertions.assertNotNull(desenvolvimento.getDataConversao());
        
        Mockito.verify(repository).save(desenvolvimento);
        Mockito.verify(pedidoService).insert(any());
    }

    @Test
    public void converterEmPedidoDeveLancarDatabaseExceptionQuandoJaConvertido() {
        // ARRANGE (Estado: já convertido)
        desenvolvimento.setVirouPedido(true);
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        // ACT & ASSERT (Verifica se impede a conversão duplicada)
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }

    @Test
    public void converterEmPedidoDeveLancarDatabaseExceptionQuandoNaoAprovado() {
        // ARRANGE (Estado: em análise)
        desenvolvimento.setStatus(StatusDesenvolvimento.EM_ANALISE);
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        // ACT & ASSERT (Verifica se impede a conversão de algo não aprovado)
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }

    @Test
    public void converterEmPedidoDeveLancarDatabaseExceptionQuandoValorConvertidoForNulo() {
        // ARRANGE (Estado: sem valor de conversão)
        desenvolvimento.setValorConvertido(null);
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        // ACT & ASSERT (Verifica se impede faturar sem preço definido)
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }
}
