package com.haackdev.commercial_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FornecedorRequest(
    @NotBlank(message = "Nome Fantasia é obrigatório")
    @Size(max = 255)
    String nomeFantasia
) {
}
