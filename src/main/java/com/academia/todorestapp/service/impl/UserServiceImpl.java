package com.academia.todorestapp.service.impl;

import com.academia.todorestapp.entities.User;
import com.academia.todorestapp.repository.UserRepository;
import com.academia.todorestapp.service.UserService;
import org.springframework.stereotype.Service;
/**
 * Сервис для выполнения операций с данными пользователя.
 *  Реализация интерфейса
 */
@Service
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void save(User user) {

        user.setPassword(user.getPassword());
        userRepository.save(user);
    }
}
