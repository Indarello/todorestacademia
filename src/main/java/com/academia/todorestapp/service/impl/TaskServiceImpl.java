package com.academia.todorestapp.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.payloads.GetTaskResponse;
import com.academia.todorestapp.repository.ListRepository;
import com.academia.todorestapp.repository.TaskRepository;
import com.academia.todorestapp.service.ListService;
import com.academia.todorestapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final ListRepository listRepository;

    private final ListService listService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ListRepository listRepository, ListService listService) {
        this.taskRepository = taskRepository;
        this.listRepository = listRepository;
        this.listService = listService;
    }

    @Override
    @Transactional
    public Optional<Task> addTask(Task task) {
        if (!checkIfListExistedAndUpdate(task, false)) return Optional.empty();

        return Optional.of(taskRepository.saveAndFlush(task));
    }

    @Override
    public Optional<List> FindListById(UUID id) {
        return listRepository.findById(id);
    }

    @Override
    public GetTaskResponse getAll(Pageable pageable, UUID id) {
        Page<Task> searchResult = taskRepository.findAllByListId(pageable, id);
        return getGetTaskResponse(searchResult);
    }

    @Override
    public GetTaskResponse getAllWithSpec(Specification<Task> spec, Pageable pageable) {
        Page<Task> searchResult = taskRepository.findAllWithSpecification(spec, pageable);
        return getGetTaskResponse(searchResult);
    }

    private GetTaskResponse getGetTaskResponse(Page<Task> searchResult) {
        java.util.List<Task> listOfTasks = searchResult.toList();
        int done = 0;
        int notDone = 0;
        for (Task task : listOfTasks) {
            if (task.isDone()) {
                done++;
            } else {
                notDone++;
            }
        }

        return new GetTaskResponse(done, notDone, searchResult);
    }

    @Override
    @Transactional
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
                boolean newDoneStatus = done.get();
                foundedTask.setDone(newDoneStatus);

                if (!checkIfListExistedAndUpdate(foundedTask, newDoneStatus)) return Optional.empty();
            } else {
                if (!checkIfListExistedAndUpdate(foundedTask)) return Optional.empty();
            }

            foundedTask.setEditDate(Timestamp.valueOf(LocalDateTime.now()));

            return Optional.of(taskRepository.saveAndFlush(foundedTask));
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean deleteTask(UUID id) {
        Optional<Task> searchResult = taskRepository.findById(id);
        if (searchResult.isPresent()) {
            Task foundedTask = searchResult.get();
            if (!foundedTask.isDone()) {
                if (!checkIfListExistedAndUpdate(foundedTask, true)) return false;
            } else {
                if (!checkIfListExistedAndUpdate(foundedTask)) return false;
            }

            taskRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public Optional<Task> markDoneTask(UUID id) {
        Optional<Task> searchResult = taskRepository.findById(id);
        if (searchResult.isPresent()) {
            Task foundedTask = searchResult.get();
            if (!foundedTask.isDone()) {
                foundedTask.setDone(true);

                if (!checkIfListExistedAndUpdate(foundedTask, true)) return Optional.empty();

                foundedTask.setEditDate(Timestamp.valueOf(LocalDateTime.now()));
                return Optional.of(taskRepository.saveAndFlush(foundedTask));
            }

            return Optional.of(foundedTask);
        }

        return Optional.empty();
    }

    private boolean checkIfListExistedAndUpdate(Task task, boolean newStatusToUpdate) {
        Optional<List> searchList = FindListById(task.getListId());
        if (searchList.isPresent()) {
            List foundedList = searchList.get();

            if (newStatusToUpdate) {
                listService.checkIfListShouldBeDone(foundedList, task.getId());
            } else {
                foundedList.setDone(false);
            }

            foundedList.setEditDate(Timestamp.valueOf(LocalDateTime.now()));
            listService.saveList(foundedList);

            return true;
        } else {
            return false;
        }
    }

    private boolean checkIfListExistedAndUpdate(Task task) {
        Optional<List> searchList = FindListById(task.getListId());
        if (searchList.isPresent()) {
            List foundedList = searchList.get();

            foundedList.setEditDate(Timestamp.valueOf(LocalDateTime.now()));
            listService.saveList(foundedList);

            return true;
        } else {
            return false;
        }
    }
}
