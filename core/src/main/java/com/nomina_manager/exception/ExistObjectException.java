package com.nomina_manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExistObjectException extends RuntimeException {
    public ExistObjectException(String message) {
        super(message);
    }
}
