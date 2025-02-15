package com.carnasa.cr.projectkingdomwebpage.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserDetailsAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleUserDetailsAlreadyExistException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponse("CONFLICT", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorResponse("BAD_REQUEST", ex.getMessage(), request.getRequestURL().toString()), HttpStatus.BAD_REQUEST);
    }

}
