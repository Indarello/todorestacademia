package com.academia.todorestapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class TodolistAuthenticationProvider implements AuthenticationProvider  {


    @Autowired
    UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {


        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if(userRepository.findByUsername(username).getPassword().equals(password)){
            return new UsernamePasswordAuthenticationToken(username,password,grantedAuthorities);
        }


        else return null;


    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
