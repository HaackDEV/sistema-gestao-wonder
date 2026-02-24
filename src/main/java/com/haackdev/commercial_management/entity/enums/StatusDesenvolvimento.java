package com.haackdev.commercial_management.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusDesenvolvimento {
    EM_ANALISE(1),
    APROVADO(2),
    REPROVADO(3);

    private final int codigo;

    public static StatusDesenvolvimento valueOf(int codigo) {
        for (StatusDesenvolvimento status : StatusDesenvolvimento.values()) {
            if (status.codigo == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Código de status inválido: " + codigo);
    }
}
