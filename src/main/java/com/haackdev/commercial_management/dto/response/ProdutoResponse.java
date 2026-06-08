package com.haackdev.commercial_management.dto.response;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String codigoProduto,
        String descricao,
        String tipo,
        String cor,
        String material,
        BigDecimal valorCusto,
        BigDecimal valorVenda,
        Long fornecedorId,
        String fornecedorNomeFantasia
) {
}
