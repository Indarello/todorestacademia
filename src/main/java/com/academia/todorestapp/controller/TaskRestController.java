package com.academia.todorestapp.controller;

import com.academia.todorestapp.dto.Dto;
import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.payloads.ApiResponse;
import com.academia.todorestapp.service.TaskService;
import com.academia.todorestapp.util.SearchOperation;
import com.academia.todorestapp.util.TaskSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CRUD REST контроллер для работы с List
 */
@RestController
public class TaskRestController {

    private final TaskService taskService;

    @Autowired
    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Добавление нового Task
     *
     * @param task - сущность задачи
     * @return ResponseEntity со статусом
     */
    @PostMapping(value = "/task", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> taskAdd(@RequestBody Task task) {
        String objName = task.getName();
        UUID objListId = task.getListId();
        String objDescription = task.getDescription();
        int objUrgency = task.getUrgency();
        if (objName == null || objListId == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter name|listId not provided"), HttpStatus.NOT_ACCEPTABLE);
        }
        if (objDescription == null) objDescription = "";
        if (objUrgency == 0) {
            objUrgency = 1; //будет превращать некоторые некорректные значения в 0 и затем в 1, считается нормальным если не будет давать ошибку?
        }

        String checkResult;

        if (!(checkResult = Task.checkName(objName)).equals("ok") || !(checkResult = Task.checkDescription(objDescription)).equals("ok")
                || !(checkResult = Task.checkUrgency(objUrgency)).equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, checkResult), HttpStatus.NOT_ACCEPTABLE);
        }

        Task newTask = new Task(task.getName(), task.getListId(), objDescription, objUrgency);

        try {
            Optional<Task> editResult = taskService.addTask(newTask);

            if (editResult.isPresent()) {
                return new ResponseEntity<>(editResult.get(), HttpStatus.CREATED);
            }

            return new ResponseEntity<>(new ApiResponse(false, "List not found by listId or database error"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Получение списков Task, с пагинацией и дополнительной инфомрацией
     *
     * @param dto - объекст с разными параметрами
     * @return ResponseEntity со статусом
     */
    @GetMapping(value = "/task", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> taskGetTasks(@RequestBody Dto dto) {
        TaskSpecificationsBuilder builder = new TaskSpecificationsBuilder();
        Pageable pageable;
        String checkResult;
        UUID objListId = dto.getListId();

        if (objListId == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter listId not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (!(checkResult = dto.checkNumberOfElements()).equals("ok") || !(checkResult = dto.checkRequestPage()).equals("ok")
                || !(checkResult = dto.checkSortParameterTask()).equals("ok") || !(checkResult = dto.checkSortType()).equals("ok")
                || dto.getFilter() != null && !(checkResult = dto.checkFilter()).equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, checkResult), HttpStatus.NOT_ACCEPTABLE);
        }

        if (dto.getSortType().equals("ascending")) {
            pageable = PageRequest.of(dto.getRequestPage(), dto.getNumberOfElements(), Sort.by(dto.getSortParameter()));
        } else {
            pageable = PageRequest.of(dto.getRequestPage(), dto.getNumberOfElements(), Sort.by(dto.getSortParameter()).descending());
        }

        if (dto.getFilter() != null) {
            Pattern pattern =
                    Pattern.compile("([A-Za-z0-9_]{2,})(" + SearchOperation.SIMPLE_OPERATION_SET +
                            ")(\\*?)([A-Za-z0-9_а-яА-Я:\\-.+\\s]+?)(\\*?),", Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(dto.getFilter() + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
            }

            Specification<Task> spec = builder.build();

            //Specification<Task> spec1 = new TaskSpecification(new SearchCriteria("listId", SearchOperation.EQUALITY, listId));  //TODO: UUID в specification

            //return new ResponseEntity<>(taskService.getAllWithSpec(Specification.where(spec1).and(spec), pageable), HttpStatus.OK);

            return new ResponseEntity<>(taskService.getAllWithSpec(spec, pageable), HttpStatus.OK);
        }

        try {
            return new ResponseEntity<>(taskService.getAll(pageable, objListId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Изменение сущности Task
     *
     * @param task - сущность задачи
     * @return ResponseEntity со статусом
     */
    @PutMapping(value = "/task", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> taskEdit(@RequestBody Task task) {
        UUID objId = task.getId();
        String objName = task.getName();
        String objDescription = task.getDescription();
        int objUrgency = task.getUrgency();
        Boolean objDone = task.getDone();
        Optional<String> OptionalName = Optional.empty();
        Optional<String> OptionalDescription = Optional.empty();
        Optional<Integer> OptionalUrgency = Optional.empty();
        Optional<Boolean> OptionalDone = Optional.empty();
        String checkResult;

        if (objId == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter id not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (objName != null) {
            if (!(checkResult = Task.checkName(objName)).equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, checkResult), HttpStatus.NOT_ACCEPTABLE);
            } else {
                OptionalName = Optional.of(objName);
            }
        }

        if (objDescription != null) {
            if (!(checkResult = Task.checkDescription(objDescription)).equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, checkResult), HttpStatus.NOT_ACCEPTABLE);
            } else {
                OptionalDescription = Optional.of(objDescription);
            }
        }

        if (Task.checkUrgency(objUrgency).equals("ok")) {
            //будет превращать некоторые некорректные значения в корректные, считается нормальным если не будет давать ошибку?
            OptionalUrgency = Optional.of(objUrgency);
        }

        if (objDone != null) OptionalDone = Optional.of(objDone);

        if (!OptionalName.isPresent() && !OptionalDescription.isPresent() && !OptionalUrgency.isPresent() && !OptionalDone.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "None of parameter name,description,urgency,done provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            Optional<Task> editResult = taskService.editTask(objId, OptionalName, OptionalDescription, OptionalUrgency, OptionalDone);

            if (editResult.isPresent()) {
                return new ResponseEntity<>(editResult.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "Task not found, or database error (List not found)"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Установление true в done для сущности Task
     *
     * @param id - id сущности
     * @return ResponseEntity со статусом
     */
    @PutMapping(value = "/task/markDone/{id}", produces = "application/json")
    public ResponseEntity<Object> taskMarkDone(@PathVariable(name = "id") UUID id) {
        if (id == null) return new ResponseEntity<>(new ApiResponse(false, "Parameter id not provided"), HttpStatus.NOT_ACCEPTABLE);

        try {
            Optional<Task> editResult = taskService.markDoneTask(id);

            if (editResult.isPresent()) {
                return new ResponseEntity<>(editResult.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "Task not found, or database error (List not found)"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Удаление сущности Task
     *
     * @param id - id сущности
     * @return ResponseEntity со статусом
     */
    @DeleteMapping(value = "/task/{id}", produces = "application/json")
    public ResponseEntity<Object> taskDelete(@PathVariable(name = "id") UUID id) {
        if (id == null) return new ResponseEntity<>(new ApiResponse(false, "Parameter id not provided"), HttpStatus.NOT_ACCEPTABLE);

        try {
            if (taskService.deleteTask(id)) {
                return new ResponseEntity<>(new ApiResponse(true, "Task deleted"), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "Task not found, or database error (List not found)"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}