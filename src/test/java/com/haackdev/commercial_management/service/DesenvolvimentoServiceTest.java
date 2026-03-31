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

    @Test
    public void converterEmPedidoDeveRetornarPedidoQuandoSucesso() {
        // Arrange
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        Mockito.when(pedidoService.insert(any())).thenAnswer(invocation -> invocation.getArgument(0)); // Retorna o proprio pedido
        
        // Act
        Pedido result = service.converterEmPedido(idExistente);
        
        // Assert
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
        desenvolvimento.setVirouPedido(true);
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }

    @Test
    public void converterEmPedidoDeveLancarDatabaseExceptionQuandoNaoAprovado() {
        desenvolvimento.setStatus(StatusDesenvolvimento.EM_ANALISE);
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }

    @Test
    public void converterEmPedidoDeveLancarDatabaseExceptionQuandoValorConvertidoForNulo() {
        desenvolvimento.setValorConvertido(null);
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(desenvolvimento));
        
        Assertions.assertThrows(DatabaseException.class, () -> service.converterEmPedido(idExistente));
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }
}
