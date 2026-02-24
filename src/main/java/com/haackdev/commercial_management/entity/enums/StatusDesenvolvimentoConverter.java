package com.haackdev.commercial_management.entity.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusDesenvolvimentoConverter implements AttributeConverter<StatusDesenvolvimento, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StatusDesenvolvimento status) {
        if (status == null) {
            return null;
        }
        return status.getCodigo();
    }

    @Override
    public StatusDesenvolvimento convertToEntityAttribute(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        return StatusDesenvolvimento.valueOf(codigo);
    }
}
