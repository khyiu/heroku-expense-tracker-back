package be.kuritsu.hetb.controller;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import be.kuritsu.hetb.exception.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
