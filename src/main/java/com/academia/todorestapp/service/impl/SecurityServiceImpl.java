package com.academia.todorestapp.service.impl;

import com.academia.todorestapp.service.SecurityService;
import com.academia.todorestapp.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Сервис для обеспечения безопасности (авторизация, аутентификация).
 */
@Service
public class SecurityServiceImpl implements SecurityService, AuthenticationProvider
{

    private final TokenProvider tokenProvider;

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityServiceImpl(TokenProvider tokenProvider, @Qualifier("userDetailsServiceImpl") UserDetailsServiceImpl userDetailsService)
    {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Funcition to find users todolist after authorization
     *
     * @return List of Notes (TodoList)
     */
    public String getUserByToken(String token)
    {
        if (tokenProvider.validateToken(token))
        {
            return tokenProvider.getUsernameFromJWT(token);
        }

        return null;
    }

    @Override
    public String findUserInUsername()
    {

        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetails != null)
        {
            return (String) userDetails;
        }

        return "NOBODY";
    }

    /**
     * Autologin
     *
     * @param username
     * @param password
     */
    @Override
    public Authentication autoLogin(String username, String password)
    {

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                        userDetails.getPassword(), userDetails.getAuthorities());

        if (usernamePasswordAuthenticationToken.isAuthenticated())
        {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);


        }
        return usernamePasswordAuthenticationToken;
    }

    @Override
    public void logout()
    {
        SecurityContextHolder.clearContext();

        //authenticationManager.authenticate(null);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {

        String username = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                        userDetails.getPassword(), userDetails.getAuthorities());


        if (usernamePasswordAuthenticationToken.isAuthenticated())
        {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        return usernamePasswordAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass)
    {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
