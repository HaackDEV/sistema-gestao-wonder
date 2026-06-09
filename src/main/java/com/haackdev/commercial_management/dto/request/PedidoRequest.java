package com.haackdev.commercial_management.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record PedidoRequest(
    @NotNull(message = "O ID do cliente é obrigatório")
    Long clienteId,
    @NotNull(message = "A data do pedido é obrigatória")
    LocalDate dataPedido,
    String condicaoPagamento,
    String parcelas,
    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    List<ItemPedidoRequest> itens
) {
}
