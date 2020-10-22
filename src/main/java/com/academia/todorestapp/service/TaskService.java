package com.academia.todorestapp.service;

import com.academia.todorestapp.entities.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

public interface TaskService {

    Task addTask(Task task);

    boolean FindListById(UUID id);

    java.util.List<Task> getAll();

    Optional<Task> editTask(UUID id, Optional<String> name, Optional<String> description, Optional<Integer> urgency, Optional<Boolean> done);

    boolean deleteTask(UUID id);
}
