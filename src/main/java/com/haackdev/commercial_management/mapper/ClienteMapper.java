package com.haackdev.commercial_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.haackdev.commercial_management.entity.Cliente;
import com.haackdev.commercial_management.dto.request.ClienteRequest;
import com.haackdev.commercial_management.dto.response.ClienteResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    Cliente requestToCliente (ClienteRequest request);

    ClienteResponse clienteToClienteResponse (Cliente cliente);
}
