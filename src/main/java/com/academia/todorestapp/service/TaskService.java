package com.academia.todorestapp.service;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.payloads.GetTaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

public interface TaskService {

    Optional<Task> addTask(Task task);

    Optional<List> FindListById(UUID id);

    GetTaskResponse getAll(Pageable pageable, UUID id);

    GetTaskResponse getAllWithSpec(Specification<Task> spec, Pageable pageable);

    Optional<Task> editTask(UUID id, Optional<String> name, Optional<String> description, Optional<Integer> urgency, Optional<Boolean> done);

    Optional<Task> markDoneTask(UUID id);

    boolean deleteTask(UUID id);
}
