package com.utkarsh.file_nest.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus
public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(String message){
        super(message);
    }

}
