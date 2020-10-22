package com.academia.todorestapp.controller;

import com.academia.todorestapp.payloads.ApiResponse;
import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.service.ListService;
import com.academia.todorestapp.service.TaskService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

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

    //TODO: доделать методы вывода списка задач и списка списков, сделать метод /task/markDone/{id}

    @PostMapping(value = "/task", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> taskAdd(@RequestBody Task obj) {
        String objName = obj.getName();
        if (objName == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter name not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String nameCheckResult = Task.checkName(objName);
        if (!nameCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, nameCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        int objUrgency = obj.getUrgency();
        if (objUrgency == 0) {
            objUrgency = 1; //будет превращать некоторые некорректные значения в 0 и затем в 1, считается нормальным если не будет давать ошибку?
        } else {
            String objUrgencyCheckResult = Task.checkUrgency(objUrgency);
            if (!objUrgencyCheckResult.equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, objUrgencyCheckResult), HttpStatus.NOT_ACCEPTABLE);
            }
        }

        String objDescription = obj.getDescription();
        if (objDescription == null) {
            objDescription = "";
        } else {
            String objDescriptionCheckResult = Task.checkDescription(objDescription);
            if (!objDescriptionCheckResult.equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, objDescriptionCheckResult), HttpStatus.NOT_ACCEPTABLE);
            }
        }

        UUID objListId = obj.getListId();
        if (objListId == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter listId not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (!taskService.FindListById(obj.getListId())) {
            return new ResponseEntity<>(new ApiResponse(false, "List not found by id"), HttpStatus.NOT_FOUND);
        }

        Task newTask = new Task(obj.getName(), obj.getListId(), objDescription, objUrgency);

        try {
            return new ResponseEntity<>(taskService.addTask(newTask), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/task", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> taskGetTasks(@RequestBody ObjectNode obj) {
        //TODO: доделать вывод списка задач
        try {
            return new ResponseEntity<>(taskService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/task", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> taskEdit(@RequestBody ObjectNode obj) {
        Optional<String> name = Optional.empty();
        Optional<String> description = Optional.empty();
        Optional<Boolean> done = Optional.empty();
        Optional<Integer> urgency = Optional.empty();

        if (!obj.has("id")) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter id not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String id = obj.get("id").asText();
        String idCheckResult = Task.checkStringId(id);
        if (!idCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, idCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        if (obj.has("name")) {
            String objName = obj.get("name").asText();
            String nameCheckResult = Task.checkName(objName);
            if (!nameCheckResult.equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, nameCheckResult), HttpStatus.NOT_ACCEPTABLE);
            } else {
                name = Optional.of(objName);
            }
        }

        if (obj.has("description")) {
            String objDescription = obj.get("description").asText();
            String objDescriptionCheckResult = Task.checkDescription(objDescription);
            if (!objDescriptionCheckResult.equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, objDescriptionCheckResult), HttpStatus.NOT_ACCEPTABLE);
            } else {
                description = Optional.of(objDescription);
            }
        }

        if (obj.has("done")) {
            //будет превращать числа и строки в bool, считается нормальным если не будет давать ошибку?
            done = Optional.of(obj.get("done").asBoolean());
        }

        if (obj.has("urgency")) {
            int objUrgency = obj.get("urgency").asInt();
            String objUrgencyCheckResult = Task.checkUrgency(objUrgency);
            if (!objUrgencyCheckResult.equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, objUrgencyCheckResult), HttpStatus.NOT_ACCEPTABLE);
            } else {
                urgency = Optional.of(objUrgency);
            }
        }

        if (name.isEmpty() && description.isEmpty() && done.isEmpty() && urgency.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse(false, "None of parameter name,description,urgency,done provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            Optional<Task> editResult = taskService.editTask(UUID.fromString(id), name, description, urgency, done);

            if (editResult.isPresent()) {
                return new ResponseEntity<>(editResult.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "Task not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/task/{id}", produces = "application/json")
    public ResponseEntity<Object> taskDelete(@PathVariable(name = "id") String id) {
        String idCheckResult = Task.checkStringId(id);
        if (!idCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, idCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            if (taskService.deleteTask(UUID.fromString(id))) {
                return new ResponseEntity<>(new ApiResponse(true, "Task deleted"), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "Task not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}