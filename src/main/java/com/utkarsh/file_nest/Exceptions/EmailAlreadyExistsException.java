package com.utkarsh.file_nest.Exceptions;




public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String message){
        super(message);
    }

}
