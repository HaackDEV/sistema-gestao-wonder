package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.ItemPedido;
import com.haackdev.commercial_management.entity.Pedido;
import com.haackdev.commercial_management.repository.PedidoRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Busca todos os pedidos cadastrados
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    // Busca um pedido pelo ID
    public Pedido findById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    // Insere um novo pedido
    public Pedido insert(Pedido pedido) {
        // Garantir o vínculo bidirecional em cada item do pedido
        for (ItemPedido itemPedido : pedido.getItens()){
            itemPedido.setPedido(pedido);
        }
        // Atribuir o valor total calculado usando a inteligência da entidade
        pedido.setValorTotal(pedido.getValorTotalCalculado());
        
        return pedidoRepository.save(pedido);
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
    public Pedido update(Long id, Pedido pedido) {
        try {
            Pedido entity = pedidoRepository.getReferenceById(id);
            updateData(entity, pedido);
            return pedidoRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Metodo auxiliar para atualizar os dados do pedido
    private void updateData(Pedido entity, Pedido novoPedido) {
        entity.setDataPedido(novoPedido.getDataPedido());
        entity.setValorTotal(novoPedido.getValorTotal());
        entity.setCondicaoPagamento(novoPedido.getCondicaoPagamento());
        entity.setParcelas(novoPedido.getParcelas());
        entity.setCliente(novoPedido.getCliente());
    }
}
