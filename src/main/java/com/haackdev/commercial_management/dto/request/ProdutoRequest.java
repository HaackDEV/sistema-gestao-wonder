package com.haackdev.commercial_management.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProdutoRequest(
        @NotNull Long fornecedorId,
        @NotNull String codigoProduto,
        @NotNull String descricao,
        String tipo,
        String cor,
        String material,
        @DecimalMin(value = "0.0", inclusive = true)BigDecimal valorCusto,
        @DecimalMin(value = "0.0", inclusive = true)BigDecimal valorVenda
) {
}
