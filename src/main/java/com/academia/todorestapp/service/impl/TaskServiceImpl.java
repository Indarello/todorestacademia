package com.academia.todorestapp.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.repository.ListRepository;
import com.academia.todorestapp.repository.TaskRepository;
import com.academia.todorestapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final ListRepository listRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ListRepository listRepository) {
        this.taskRepository = taskRepository;
        this.listRepository = listRepository;
    }

    //TODO:любые изменения с задачами могут менять статус сделанности и последнее изменения списка дел к которому они принадлежат

    @Override
    public Task addTask(Task task) {
        return taskRepository.saveAndFlush(task);
    }

    @Override
    public boolean FindListById(UUID id) {
        return listRepository.existsById(id);
    }

    @Override
    public java.util.List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> editTask(UUID id, Optional<String> name, Optional<String> description, Optional<Integer> urgency, Optional<Boolean> done) {
        Optional<Task> searchResult = taskRepository.findById(id);
        if (searchResult.isPresent()) {
            Task foundedTask = searchResult.get();
            if (name.isPresent()) {
                foundedTask.setName(name.get());
            }
            if (description.isPresent()) {
                foundedTask.setDescription(description.get());
            }
            if (urgency.isPresent()) {
                foundedTask.setUrgency(urgency.get());
            }
            if (done.isPresent()) {
                foundedTask.setDone(done.get());
            }
            foundedTask.setEditDate(Timestamp.valueOf(LocalDateTime.now()));

            return Optional.of(taskRepository.saveAndFlush(foundedTask));
        }

        return Optional.empty();
    }

    @Override
    public boolean deleteTask(UUID id) {
        Optional<Task> searchResult = taskRepository.findById(id);
        if (searchResult.isPresent()) {
            taskRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
