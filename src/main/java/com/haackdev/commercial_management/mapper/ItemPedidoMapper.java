package com.haackdev.commercial_management.mapper;

import com.haackdev.commercial_management.dto.request.ItemPedidoRequest;
import com.haackdev.commercial_management.dto.response.ItemPedidoResponse;
import com.haackdev.commercial_management.entity.ItemPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemPedidoMapper {

    // Converte de Request para Entidade
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "produto.id", source = "produtoId")
    ItemPedido requestToItemPedido(ItemPedidoRequest request);

    // Converte de Entidade para Response
    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "produtoDescricao", source = "produto.descricao")
    @Mapping(target = "subTotal", expression = "java(entity.getSubTotal())")
    ItemPedidoResponse itemPedidoToItemPedidoResponse(ItemPedido entity);
}
