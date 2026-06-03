package com.haackdev.commercial_management.mapper;

import com.haackdev.commercial_management.dto.request.FornecedorRequest;
import com.haackdev.commercial_management.dto.response.FornecedorResponse;
import com.haackdev.commercial_management.entity.Fornecedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FornecedorMapper {

    @Mapping(target = "id", ignore = true)
    Fornecedor requestToFornecedor (FornecedorRequest request); // Converte um FornecedorRequest para Fornecedor

    FornecedorResponse fornecedorToFornecedorResponse (Fornecedor fornecedor); // Converte um Fornecedor para FornecedorResponse
}