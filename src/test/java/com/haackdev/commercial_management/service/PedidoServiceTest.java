package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.ItemPedidoRequest;
import com.haackdev.commercial_management.dto.request.PedidoRequest;
import com.haackdev.commercial_management.dto.response.ItemPedidoResponse;
import com.haackdev.commercial_management.dto.response.PedidoResponse;
import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.entity.ItemPedido;
import com.haackdev.commercial_management.entity.Pedido;
import com.haackdev.commercial_management.mapper.PedidoMapper;
import com.haackdev.commercial_management.repository.ClienteRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Testes Unitários da Camada de Serviço para a entidade Pedido.
 * Refatorado para o padrão DTO.
 */
@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService service;

    private Long idExistente;
    private Long idInexistente;
    private Pedido pedido;
    private Cliente cliente;
    private PedidoRequest requestDTO;
    private PedidoResponse responseDTO;

    @BeforeEach
    void setUp() throws Exception {
        idExistente = 1L;
        idInexistente = 99L;

        cliente = new Cliente();
        cliente.setId(10L);
        cliente.setNomeFantasia("Cliente Teste");

        pedido = new Pedido();
        pedido.setId(idExistente);
        pedido.setCliente(cliente);

        ItemPedido item = new ItemPedido();
        item.setId(20L);
        item.setQuantidade(2);
        item.setValorUnitario(new BigDecimal("50.00"));
        pedido.addItem(item);

        ItemPedidoRequest itemRequest = new ItemPedidoRequest(30L, 2, new BigDecimal("50.00"));
        requestDTO = new PedidoRequest(
                10L, LocalDate.now(), "Boleto", "1x", List.of(itemRequest)
        );

        ItemPedidoResponse itemResponse = new ItemPedidoResponse(20L, 30L, "Produto Teste", 2, new BigDecimal("50.00"), new BigDecimal("100.00"));
        responseDTO = new PedidoResponse(
                idExistente, 10L, "Cliente Teste", LocalDate.now(), new BigDecimal("100.00"), "Boleto", "1x", List.of(itemResponse)
        );
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE CONSULTA
    // -------------------------------------------------------------

    @Test
    public void findAllDeveRetornarListaDePedidoResponse() {
        Mockito.when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        Mockito.when(pedidoMapper.pedidoToPedidoResponse(pedido)).thenReturn(responseDTO);
        
        List<PedidoResponse> result = service.findAll();
        
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(idExistente, result.get(0).id());
    }

    @Test
    public void findByIdDeveRetornarPedidoResponseQuandoIdExistir() {
        Mockito.when(pedidoRepository.findById(idExistente)).thenReturn(Optional.of(pedido));
        Mockito.when(pedidoMapper.pedidoToPedidoResponse(pedido)).thenReturn(responseDTO);
        
        PedidoResponse result = service.findById(idExistente);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
    }

    @Test
    public void findByIdDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE INSERÇÃO
    // -------------------------------------------------------------

    @Test
    public void insertDeveSalvarERetornarPedidoResponse() {
        Mockito.when(pedidoMapper.requestToPedido(any())).thenReturn(pedido);
        Mockito.when(clienteRepository.existsById(10L)).thenReturn(true);
        Mockito.when(clienteRepository.getReferenceById(10L)).thenReturn(cliente);
        Mockito.when(pedidoRepository.save(any())).thenReturn(pedido);
        Mockito.when(pedidoMapper.pedidoToPedidoResponse(any())).thenReturn(responseDTO);
        
        PedidoResponse result = service.insert(requestDTO);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
        Mockito.verify(pedidoRepository).save(any());
    }

    @Test
    public void insertDeveLancarResourceNotFoundExceptionQuandoClienteNaoExistir() {
        Mockito.when(pedidoMapper.requestToPedido(any())).thenReturn(pedido);
        Mockito.when(clienteRepository.existsById(10L)).thenReturn(false);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.insert(requestDTO));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE EXCLUSÃO
    // -------------------------------------------------------------

    @Test
    public void deleteDeveNaoFazerNadaQuandoIdExistir() {
        Mockito.when(pedidoRepository.existsById(idExistente)).thenReturn(true);
        Mockito.doNothing().when(pedidoRepository).deleteById(idExistente);
        
        Assertions.assertDoesNotThrow(() -> service.delete(idExistente));
    }

    @Test
    public void deleteDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(pedidoRepository.existsById(idInexistente)).thenReturn(false);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(idInexistente));
    }

    // -------------------------------------------------------------
    // CENÁRIOS DE ATUALIZAÇÃO
    // -------------------------------------------------------------

    @Test
    public void updateDeveRetornarPedidoResponseQuandoIdExistir() {
        Mockito.when(pedidoRepository.getReferenceById(idExistente)).thenReturn(pedido);
        Mockito.when(clienteRepository.existsById(10L)).thenReturn(true);
        Mockito.when(clienteRepository.getReferenceById(10L)).thenReturn(cliente);
        Mockito.when(pedidoMapper.requestToPedido(any())).thenReturn(pedido);
        Mockito.when(pedidoRepository.save(any())).thenReturn(pedido);
        Mockito.when(pedidoMapper.pedidoToPedidoResponse(any())).thenReturn(responseDTO);
        
        PedidoResponse result = service.update(idExistente, requestDTO);
        
        Assertions.assertNotNull(result);
        Assertions.assertEquals(idExistente, result.id());
    }

    @Test
    public void updateDeveLancarResourceNotFoundExceptionQuandoIdNaoExistir() {
        Mockito.when(pedidoRepository.getReferenceById(idInexistente)).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(idInexistente, requestDTO));
    }
}
