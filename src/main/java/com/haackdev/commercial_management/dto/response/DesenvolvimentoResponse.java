package com.haackdev.commercial_management.dto.response;

import com.haackdev.commercial_management.entity.enums.StatusDesenvolvimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DesenvolvimentoResponse(
    Long id,
    Long clienteId,
    String clienteNomeFantasia,
    Long produtoId,
    String codigoProduto,
    String descricaoProduto,
    String tipo,
    LocalDate dataSolicitacao,
    StatusDesenvolvimento status,
    String motivoReprovacao,
    Boolean virouPedido,
    BigDecimal valorConvertido,
    LocalDate dataConversao
) {
}
