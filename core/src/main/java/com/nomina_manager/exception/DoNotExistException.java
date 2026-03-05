package com.nomina_manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DoNotExistException extends RuntimeException {
    public DoNotExistException(String message) {
        super(message);
    }
}
