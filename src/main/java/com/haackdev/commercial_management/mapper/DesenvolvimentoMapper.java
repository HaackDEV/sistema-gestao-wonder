package com.haackdev.commercial_management.mapper;

import com.haackdev.commercial_management.dto.request.DesenvolvimentoRequest;
import com.haackdev.commercial_management.dto.response.DesenvolvimentoResponse;
import com.haackdev.commercial_management.entity.Desenvolvimento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DesenvolvimentoMapper {

    @Mapping(target = "id", ignore = true)
    Desenvolvimento requestToDesenvolvimento (DesenvolvimentoRequest request); // Converte um DesenvolvimentoRequest para Desenvolvimento

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "clienteNomeFantasia", source = "cliente.nomeFantasia")
    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "codigoProduto", source = "produto.codigoProduto")
    @Mapping(target = "descricaoProduto", source = "produto.descricao")
    DesenvolvimentoResponse DesenvolvimentoToDesenvolvimentoResponse (Desenvolvimento Desenvolvimento);// Converte um Desenvolvimento para DesenvolvimentoResponse
}