package me.victor.cbd.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RestException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
