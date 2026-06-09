package com.haackdev.commercial_management.mapper;

import com.haackdev.commercial_management.dto.request.PedidoRequest;
import com.haackdev.commercial_management.dto.response.PedidoResponse;
import com.haackdev.commercial_management.entity.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ItemPedidoMapper.class})
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "valorTotal", ignore = true) // Calculado no Service/Entity
    @Mapping(target = "cliente.id", source = "clienteId")
    Pedido requestToPedido(PedidoRequest request);

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNomeFantasia", source = "cliente.nomeFantasia")
    PedidoResponse pedidoToPedidoResponse(Pedido entity);
}
