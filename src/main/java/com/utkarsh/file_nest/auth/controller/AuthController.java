package com.utkarsh.file_nest.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.utkarsh.file_nest.auth.dto.AuthResponse;
import com.utkarsh.file_nest.auth.dto.LoginRequest;
import com.utkarsh.file_nest.auth.dto.RegisterRequest;
import com.utkarsh.file_nest.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


private final AuthService authService;


public AuthController(AuthService authService){
    this.authService = authService;
}


@PostMapping(path = "/login")
public ResponseEntity<AuthResponse> login( @Valid @RequestBody LoginRequest request){
    return ResponseEntity.ok(authService.login(request));
} 

@PostMapping(path = "/register")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
    return ResponseEntity.ok(authService.register(request));
}
}
