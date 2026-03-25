package com.haackdev.commercial_management.service;

import com.haackdev.commercial_management.entity.ItemPedido;
import com.haackdev.commercial_management.repository.ItemPedidoRepository;
import com.haackdev.commercial_management.service.exceptions.DatabaseException;
import com.haackdev.commercial_management.service.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemPedidoService {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    // Busca todos os itens de pedidos cadastrados
    public List<ItemPedido> findAll() {
        return itemPedidoRepository.findAll();
    }

    // Busca um item de pedido pelo ID
    public ItemPedido findById(Long id) {
        return itemPedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    // Insere um novo item de pedido
    public ItemPedido insert(ItemPedido itemPedido) {
        return itemPedidoRepository.save(itemPedido);
    }

    // Deleta um item de pedido pelo ID
    public void delete(Long id) {
        if (!itemPedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            itemPedidoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(
                    "Não é possível deletar um item de pedido pois ele possui vínculos no banco de dados.");
        }
    }

    // Atualiza um item de pedido existente
    public ItemPedido update(Long id, ItemPedido itemPedido) {
        try {
            ItemPedido entity = itemPedidoRepository.getReferenceById(id);
            updateData(entity, itemPedido);
            return itemPedidoRepository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    // Metodo auxiliar para atualizar os dados do item de pedido
    private void updateData(ItemPedido entity, ItemPedido novoItemPedido) {
        entity.setQuantidade(novoItemPedido.getQuantidade());
        entity.setValorUnitario(novoItemPedido.getValorUnitario());
        entity.setProduto(novoItemPedido.getProduto());
        entity.setPedido(novoItemPedido.getPedido());
    }
}
