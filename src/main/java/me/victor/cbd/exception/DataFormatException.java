package me.victor.cbd.exception;

import org.springframework.http.HttpStatus;

public class DataFormatException extends RestException{
    public DataFormatException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
