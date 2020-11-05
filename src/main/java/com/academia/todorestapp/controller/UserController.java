package com.academia.todorestapp.controller;

import com.academia.todorestapp.entities.User;
import com.academia.todorestapp.payloads.ApiResponse;
import com.academia.todorestapp.payloads.AuthenticationResponse;
import com.academia.todorestapp.service.SecurityService;
import com.academia.todorestapp.token.TokenProvider;
import com.sample.payloads.Login;
import com.academia.todorestapp.service.UserService;
import com.academia.todorestapp.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CRUD REST контроллер для регистрации и авторизации
 */
@RestController
public class UserController
{
    private final UserService userService;

    private final SecurityService securityService;

    private final TokenProvider tokenProvider;

    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, SecurityService securityService, TokenProvider tokenProvider, UserValidator userValidator)
    {
        this.userService = userService;
        this.securityService = securityService;
        this.tokenProvider = tokenProvider;
        this.userValidator = userValidator;
    }


    @PostMapping(path = "/registration", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> registration(@RequestBody Login login)
    {

        String token = "Token is not generated, please sign in using /login";
        User user = new User(login.getUsername(), login.getPassword());

        ApiResponse valid = userValidator.validateRegistration(login);

        if (valid.getSuccess())
        {
            userService.save(user);

            try
            {
                token = tokenProvider.createToken(securityService.autoLogin(user.getUsername(), user.getPassword()));
            } catch (Exception e)
            {
                return new ResponseEntity(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity(new AuthenticationResponse(token)
                    , HttpStatus.OK);
        }

        return new ResponseEntity(
                valid, HttpStatus.BAD_REQUEST);
    }


    @CrossOrigin("/*")
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    ResponseEntity<?> login(@RequestBody Login login)
    {

        String token;

        boolean valid = userValidator.validateAuthorization(login);

        if (valid == false)
        {
            return new ResponseEntity(
                    new ApiResponse(false, "Username is not found"), HttpStatus.BAD_REQUEST);
        }

        token = tokenProvider.createToken(securityService.autoLogin(login.getUsername(), login.getPassword()));

        return new ResponseEntity(new AuthenticationResponse(token), HttpStatus.OK);
    }

}
