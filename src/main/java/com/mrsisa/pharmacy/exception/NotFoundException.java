package com.mrsisa.pharmacy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends EntityNotFoundException {
    public NotFoundException(String message) {
        super(message);
    }

}
