package com.haackdev.commercial_management.dto.response;

import java.math.BigDecimal;

public record ItemPedidoResponse(
    Long id,
    Long produtoId,
    String produtoDescricao,
    Integer quantidade,
    BigDecimal valorUnitario,
    BigDecimal subTotal
) {
}
