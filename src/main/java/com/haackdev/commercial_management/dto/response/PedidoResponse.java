package com.haackdev.commercial_management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoResponse(
    Long id,
    Long clienteId,
    String clienteNomeFantasia,
    LocalDate dataPedido,
    BigDecimal valorTotal,
    String condicaoPagamento,
    String parcelas,
    List<ItemPedidoResponse> itens
) {
}
