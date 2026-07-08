package com.utkarsh.file_nest.auth.service;

import org.springframework.stereotype.Service;

import com.utkarsh.file_nest.auth.dto.AuthResponse;
import com.utkarsh.file_nest.auth.dto.LoginRequest;
import com.utkarsh.file_nest.auth.dto.RegisterRequest;
import com.utkarsh.file_nest.repository.UserRepository;

@Service
public class AuthService {


private final UserRepository userRepository;


public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
}



public AuthResponse login(LoginRequest request){
    return new AuthResponse();
    
}

public AuthResponse register(RegisterRequest request){
    return new AuthResponse();
}

}
