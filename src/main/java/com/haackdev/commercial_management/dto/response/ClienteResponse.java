package com.haackdev.commercial_management.dto.response;

public record ClienteResponse(
    Long id,
    String razaoSocial,
    String nomeFantasia,
    String cnpj,
    String emailGeral,
    String cidade,
    String estado
) {
}
