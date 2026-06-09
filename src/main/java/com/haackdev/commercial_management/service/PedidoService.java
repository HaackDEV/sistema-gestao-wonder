package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.dto.request.PedidoRequest;
import com.haackdev.commercial_management.dto.response.PedidoResponse;
import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.entity.ItemPedido;
import com.haackdev.commercial_management.entity.Pedido;
import com.haackdev.commercial_management.mapper.PedidoMapper;
import com.haackdev.commercial_management.repository.ClienteRepository;
import com.haackdev.commercial_management.repository.PedidoRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoMapper pedidoMapper;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.pedidoMapper = pedidoMapper;
    }

    // Busca todos os pedidos cadastrados
    public List<PedidoResponse> findAll() {
        return pedidoRepository.findAll().stream().map(pedidoMapper::pedidoToPedidoResponse)
                .toList();
    }

    // Busca um pedido pelo ID
    public PedidoResponse findById(Long id) {
        return pedidoRepository.findById(id).map(pedidoMapper::pedidoToPedidoResponse)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    // Insere um novo pedido
    public PedidoResponse insert(PedidoRequest request) {
        Pedido pedido = pedidoMapper.requestToPedido(request);
        pedido.setId(null);

        // Verificação de integridade do Cliente
        if (!clienteRepository.existsById(request.clienteId())) {
            throw new ResourceNotFoundException(request.clienteId());
        }
        Cliente cliente = clienteRepository.getReferenceById(request.clienteId());
        pedido.setCliente(cliente);

        // Vínculo bidirecional para itens
        for (ItemPedido item : pedido.getItens()) {
            item.setPedido(pedido);
        }

        pedido.setValorTotal(pedido.getValorTotalCalculado());
        pedido = pedidoRepository.save(pedido);
        return pedidoMapper.pedidoToPedidoResponse(pedido);
    }

    @Transactional
    // Deleta um pedido pelo ID
    public void delete(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            pedidoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não é possível deletar um pedido pois ele possui vínculos no banco de dados.");
        }
    }

    @Transactional
    // Atualiza um pedido existente
    public PedidoResponse update(Long id, PedidoRequest request) {
        try {
            Pedido entity = pedidoRepository.getReferenceById(id);
            updateData(entity, request);

            // Verificação de integridade do Cliente
            if (!clienteRepository.existsById(request.clienteId())) {
                throw new ResourceNotFoundException(request.clienteId());
            }
            Cliente cliente = clienteRepository.getReferenceById(request.clienteId());
            entity.setCliente(cliente);

            entity.getItens().clear();
            Pedido pedidoTemporario = pedidoMapper.requestToPedido(request);
            for (ItemPedido novoItem : pedidoTemporario.getItens()) {
                entity.addItem(novoItem);
            }

            entity.setValorTotal(entity.getValorTotalCalculado());

            entity = pedidoRepository.save(entity);
            return pedidoMapper.pedidoToPedidoResponse(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Método auxiliar para atualizar dados básicos
    private void updateData(Pedido entity, PedidoRequest request) {
        entity.setDataPedido(request.dataPedido());
        entity.setCondicaoPagamento(request.condicaoPagamento());
        entity.setParcelas(request.parcelas());
    }
}
