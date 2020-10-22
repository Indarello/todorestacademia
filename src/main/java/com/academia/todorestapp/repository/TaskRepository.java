package com.academia.todorestapp.repository;

import com.academia.todorestapp.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Интерфейс от JPA для работы в бд с таблицей List
 */

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

}