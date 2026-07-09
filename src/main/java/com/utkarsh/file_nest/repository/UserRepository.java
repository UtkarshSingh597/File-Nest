package com.utkarsh.file_nest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utkarsh.file_nest.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
