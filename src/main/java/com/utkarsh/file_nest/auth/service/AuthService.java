package com.utkarsh.file_nest.auth.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.utkarsh.file_nest.Exceptions.EmailAlreadyExistsException;
import com.utkarsh.file_nest.Exceptions.InvalidCredentialsException;
import com.utkarsh.file_nest.auth.dto.AuthResponse;
import com.utkarsh.file_nest.auth.dto.LoginRequest;
import com.utkarsh.file_nest.auth.dto.RegisterRequest;
import com.utkarsh.file_nest.entity.User;
import com.utkarsh.file_nest.repository.UserRepository;



@Service
public class AuthService {


private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;


public AuthService(UserRepository userRepository,
                    PasswordEncoder passwordEncoder
) {
    this.userRepository = userRepository;
    this.passwordEncoder= passwordEncoder;
}


public AuthResponse login(LoginRequest request){
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UsernameNotFoundException("User Does not Exists"));

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("Invalid Email or Password");
        }
    return new AuthResponse("login successful");
    
}

public AuthResponse register(RegisterRequest request){
    Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

    if(existingUser.isPresent()){
        
            throw new EmailAlreadyExistsException ("This Email Already Exists");
    
    }

    String hashedPassword = passwordEncoder.encode(request.getPassword());

    User user = new User(
        request.getName(),
        request.getEmail(),
        hashedPassword

    );

   User savedUser = userRepository.save(user);


    return new AuthResponse("dummy-Token");
}

}
