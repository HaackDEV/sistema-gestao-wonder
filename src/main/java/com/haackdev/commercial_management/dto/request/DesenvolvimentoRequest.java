package com.haackdev.commercial_management.dto.request;

import com.haackdev.commercial_management.entity.enums.StatusDesenvolvimento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DesenvolvimentoRequest(
        @NotNull Long clienteId,
        @NotNull Long produtoId,
        @NotBlank String tipo,
        @NotNull LocalDate dataSolicitacao,
        @NotNull StatusDesenvolvimento status,
        String motivoReprovacao,
        Boolean virouPedido,
        @DecimalMin(value = "0.0", inclusive = true)BigDecimal valorConvertido,
        LocalDate dataConversao
) {
}
