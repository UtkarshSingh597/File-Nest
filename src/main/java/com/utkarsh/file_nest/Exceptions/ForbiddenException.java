package com.utkarsh.file_nest.Exceptions;

public class FolderAlreadyExistsException extends RuntimeException{
    public FolderAlreadyExistsException(String message){
        super(message);
    }
}
