package com.utkarsh.file_nest.auth.service;

import com.utkarsh.file_nest.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LoggedUser {

    public User getLoggedUser() {
        return (User)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
