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

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(FolderNotFoundException.class)
         public ResponseEntity<?>handleFolderExists(FolderNotFoundException ex){
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
@ExceptionHandler(FolderAccessDenailedException.class)
    public ResponseEntity<?>handleFolderAccess(FolderAccessDenailedException ex){
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
}
        @ExceptionHandler(FolderAlreadyExistsException.class)
    public ResponseEntity<?>handleFolderAlreadyExisits(FolderAlreadyExistsException ex){
         return  ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }
 

