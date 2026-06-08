package com.haackdev.commercial_management.mapper;

import com.haackdev.commercial_management.dto.request.ProdutoRequest;
import com.haackdev.commercial_management.dto.response.ProdutoResponse;
import com.haackdev.commercial_management.entity.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProdutoMapper {

    @Mapping(target = "id", ignore = true)
    Produto requestToProduto (ProdutoRequest request); // Converte um ProdutoRequest para Produto

    @Mapping(target = "fornecedorId", source = "fornecedor.id")
    @Mapping(target = "fornecedorNomeFantasia", source = "fornecedor.nomeFantasia")
    ProdutoResponse ProdutoToProdutoResponse (Produto Produto);// Converte um Produto para ProdutoResponse
}