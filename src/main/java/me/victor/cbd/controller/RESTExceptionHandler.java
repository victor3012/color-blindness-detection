package me.victor.cbd.controller;

import me.victor.cbd.exception.APIError;
import me.victor.cbd.exception.RestException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RESTExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RestException.class)
    protected ResponseEntity<APIError> handleResourceException(RestException ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(getError(ex.getStatus(), ex), ex.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    protected APIError handleUnknownException(RuntimeException ex) {
        ex.printStackTrace();
        return new APIError(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private APIError getError(HttpStatus status, Exception ex) {
        return new APIError(status, ex.getMessage(), ex);
    }
}