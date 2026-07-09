package com.utkarsh.file_nest.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {


    public RegisterRequest() {
    }

@NotBlank
private String name;

@NotBlank
@Email
private String email;

@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\\$%^&*()-+]).{8,}$",
        message = "Password must contain at Least 8 characters and include number, upper case, lower case, and special character")

private String password;

    public String getName() {
        return name;
    }

    public void setUsername(String Name) {
        this.name = Name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
