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

/**
 * Testes Unitários da Camada de Serviço para a entidade Pedido.
 * Usa Mockito para isolar a lógica de negócio das dependências de banco de dados.
 */
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

    /**
     * Preparação inicial, ocorre ANTES de cada método de teste ser executado.
     * Serve para configurar os cenários comuns de Mock.
     */
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

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarLista() {
        // ARRANGE
        Mockito.when(repository.findAll()).thenReturn(List.of(pedido));
        
        // ACT
        List<Pedido> result = service.findAll();
        
        // ASSERT
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    public void findByIdDeveRetornarPedidoQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.findById(idExistente)).thenReturn(Optional.of(pedido));
        
        // ACT
        Pedido result = service.findById(idExistente);
        
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
    public void insertDeveCalcularTotalEConfigurarItens() {
        // ARRANGE
        Mockito.when(repository.save(any())).thenReturn(pedido);
        
        // Criando um pedido novo (id null) para simular inserção
        Pedido novoPedido = new Pedido();
        ItemPedido novoItem = new ItemPedido();
        novoItem.setId(99L);
        novoItem.setQuantidade(3);
        novoItem.setValorUnitario(new BigDecimal("10.00"));
        novoPedido.addItem(novoItem);
        
        // ACT
        Pedido result = service.insert(novoPedido);
        
        // ASSERT
        Assertions.assertNotNull(result);
        Assertions.assertNull(novoPedido.getId()); // O service deve garantir id null para novos registros
        Assertions.assertNull(novoItem.getId()); // O service deve garantir id null nos itens
        Assertions.assertEquals(new BigDecimal("30.00"), novoPedido.getValorTotal()); // 3 * 10
        Assertions.assertEquals(novoPedido, novoItem.getPedido()); // Vínculo bidirecional verificado
        
        Mockito.verify(repository).save(novoPedido);
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

    // -------------------------------------------------------------
    // CENÁRIOS DE ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarPedidoQuandoIdExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idExistente)).thenReturn(pedido);
        Mockito.when(repository.save(any())).thenReturn(pedido);
        
        // ACT
        Pedido result = service.update(idExistente, pedido);
        
        // ASSERT
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        // ARRANGE
        Mockito.when(repository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        
        // ACT & ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, pedido));
    }
}
