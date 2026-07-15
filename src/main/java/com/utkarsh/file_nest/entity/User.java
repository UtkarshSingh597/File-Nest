package com.utkarsh.file_nest.entity;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "users")
public class User {

public User(){
    
}

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private String name;

@Column(nullable = false, unique = true)
private String email;

@Column(nullable = false)
private String password;

private LocalDate createdAt = LocalDate.now();


public User( String name, String email, String password) {
   
    this.name = name;
    this.email = email;
    this.password = password;
   
}


public Long getId() {
    return id;
}


public void setId(Long id) {
    this.id = id;
}


public String getName() {
    return name;
}


public void setName(String name) {
    this.name = name;
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


public LocalDate getCreatedAt() {
    return createdAt;
}


public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
}


}
