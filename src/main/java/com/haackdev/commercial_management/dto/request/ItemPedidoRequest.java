package com.haackdev.commercial_management.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemPedidoRequest(
    @NotNull(message = "O ID do produto é obrigatório")
    Long produtoId,

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    Integer quantidade,

    @NotNull(message = "O valor unitário é obrigatório")
    @Positive(message = "O valor unitário deve ser maior que zero")
    BigDecimal valorUnitario
) {
}
