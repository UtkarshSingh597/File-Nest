package com.utkarsh.file_nest.auth.dto;

public class AuthResponse {



    public AuthResponse() {
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthResponse(String token) {
        this.token = token;
    }

    
}
