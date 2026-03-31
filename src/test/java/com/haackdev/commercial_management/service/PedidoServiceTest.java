package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.ItemPedido;
import com.haackdev.commercial_management.entity.Pedido;
import com.haackdev.commercial_management.repository.PedidoRepository;
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

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository repository;

    @InjectMocks
    private PedidoService service;

    private Long idExistente;
    private Long idInexistente;
    private Pedido pedido;
    private ItemPedido item;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 2L;
        
        pedido = new Pedido();
        pedido.setId(idExistente);
        
        item = new ItemPedido();
        item.setId(10L);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("50.00"));
        
        pedido.addItem(item);
    }

    @Test
    public void findAllDeveRetornarLista() {
        Mockito.when(repository.findAll()).thenReturn(List.of(pedido));
        List<Pedido> result = service.findAll();
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void findByIdDeveRetornarPedidoQuandoIdExistir() {
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(pedido));
        Pedido result = service.findById(idExistente);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }

    @Test
    public void insertDeveCalcularTotalEConfigurarItens() {
        Mockito.when(repository.save(any())).thenReturn(pedido);
        
        // Arrange com um pedido novo (id null)
        Pedido novoPedido = new Pedido();
        ItemPedido novoItem = new ItemPedido();
        novoItem.setId(99L);
        novoItem.setQuantidade(3);
        novoItem.setValorUnitario(new BigDecimal("10.00"));
        novoPedido.addItem(novoItem);
        
        // Act
        Pedido result = service.insert(novoPedido);
        
        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertNull(novoPedido.getId()); // O service seta null
        Assertions.assertNull(novoItem.getId()); // O service seta null no item
        Assertions.assertEquals(new BigDecimal("30.00"), novoPedido.getValorTotal()); // 3 * 10
        Assertions.assertEquals(novoPedido, novoItem.getPedido()); // Vinculo bidirecional
        
        Mockito.verify(repository).save(novoPedido);
    }

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        Mockito.when(repository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(idExistente);
        Assertions.assertDoesNotThrow(() -> service.delete(idExistente));
    }

    @Test
    public void updateDeveRetornarPedidoQuandoIdExistir() {
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(pedido);
        Mockito.when(repository.save(any())).thenReturn(pedido);
        
        Pedido result = service.update(idExistente, pedido);
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, pedido));
    }
}
