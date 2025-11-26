package com.carsplatform.backend.common.resourceExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resourceName, Object fieldValue) {
        super(String.format("Resource with %s '%s' already exists.", resourceName, fieldValue));
    }
}
