package com.utkarsh.file_nest.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


     @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<?> handleEmailExists (EmailAlreadyExistsException ex){
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());    
        }
        
    }
 

