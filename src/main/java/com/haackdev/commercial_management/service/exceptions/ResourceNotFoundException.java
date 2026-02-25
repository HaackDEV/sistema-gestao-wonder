package com.haackdev.commercial_management.service.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Object id) {
        super("Recurso não encontrado: " + id);
    }
}
