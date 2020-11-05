package com.academia.todorestapp.repository;

import com.academia.todorestapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс от JPA для работы в бд с таблицей User
 */
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
