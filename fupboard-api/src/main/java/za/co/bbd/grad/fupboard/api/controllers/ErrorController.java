package za.co.bbd.grad.fupboard.api.controllers;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import za.co.bbd.grad.fupboard.api.models.ApiError;

@ControllerAdvice
public class ErrorController {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ApiError.DATA_INTEGRITY.response();
    }
    
    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<?> handlePsql(PSQLException e) {
        return ApiError.DATA_INTEGRITY.response();
    }
}
