package com.utkarsh.file_nest.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


     @ExceptionHandler(ConflictException.class)
        public ResponseEntity<?> conflictException (ConflictException ex){
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());    
        }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<?> unauthorizedException(UnAuthorizedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
         public ResponseEntity<?>notFoundException(NotFoundException ex){
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
@ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?>handleFolderAccess(ForbiddenException ex){
         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
}
        @ExceptionHandler(BadRequest.class)
    public ResponseEntity<?>handleEmptyFileException(BadRequest ex){
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

        @ExceptionHandler(NoContentException.class)
    public ResponseEntity<?>handleNoContentException(NoContentException ex){
         return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ex.getMessage());
        }
    }
 
