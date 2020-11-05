package com.academia.todorestapp.validator;

import com.academia.todorestapp.payloads.ApiResponse;
import com.academia.todorestapp.service.UserService;
import com.sample.payloads.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Класс для валидации данных пользователя при регистрации и авторизации.
 */

@Component
public class UserValidator{

    @Autowired
    UserService userService;


    public ApiResponse validateRegistration(Login login) {

        if(userService.findByUsername(login.getUsername())!=null){
           return new ApiResponse(false,"Username is already taken");
        }

        if(login.getUsername().length()<5 || login.getUsername().length() > 25){
            return new ApiResponse(false,"Username is must be in 5 to 25 symbols");
        }

        if(login.getPassword().length() < 6 || login.getPassword().length() > 45){
            return new ApiResponse(false,"Password is must be in 6 to 45 symbols");
        }
        return new ApiResponse(true, "Username registered");
    }

    public boolean validateAuthorization(Login login){

        if(userService.findByUsername(login.getUsername())!=null){
            return true;
        }
        return false;
    }

}
