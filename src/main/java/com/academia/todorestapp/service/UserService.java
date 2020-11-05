package com.academia.todorestapp.service;


import com.academia.todorestapp.entities.User;

/**
 * Сервис для выполнения операций с данными пользователя.
 */
public interface UserService {

    User findByUsername(String username);
    void save(User user);
}
