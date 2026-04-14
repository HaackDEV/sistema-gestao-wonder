package com.haackdev.commercial_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
    @NotBlank(message = "Razão Social é obrigatória")
    String razaoSocial,

    @NotBlank(message = "Nome Fantasia é obrigatório")
    String nomeFantasia,

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(min = 14, max = 14, message = "CNPJ deve ter 14 dígitos")
    String cnpj,

    String enderecoCompleto,
    String cidade,

    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    String estado,

    String telefoneGeral,

    @Email(message = "Email inválido")
    String emailGeral,

    String contatoDesenvolvimento,
    String contatoCompras,
    String condicoesPagamento
) {
}