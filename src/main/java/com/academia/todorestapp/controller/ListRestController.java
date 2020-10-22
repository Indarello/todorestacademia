package com.academia.todorestapp.controller;

import com.academia.todorestapp.payloads.ApiResponse;
import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.service.ListService;
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
public class ListRestController {

    private final ListService listService;

    @Autowired
    public ListRestController(ListService listService) {
        this.listService = listService;
    }

    @PostMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listAdd(@RequestBody List obj) {
        String objName = obj.getName();
        if (objName == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter name not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String nameCheckResult = List.checkName(objName);
        if (!nameCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, nameCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        List newList = new List(obj.getName());

        try {
            return new ResponseEntity<>(listService.addList(newList), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listGetLists(@RequestBody ObjectNode obj) {
        //сортировка пагинация и тд позже
        //if (obj.has("sortType")) ...
        try {
            return new ResponseEntity<>(listService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listEdit(@RequestBody ObjectNode obj) {
        if (!obj.has("id") || !obj.has("name")) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter id or name not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String id = obj.get("id").asText();
        String idCheckResult = List.checkId(id);
        if (!idCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, idCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        String name = obj.get("name").asText();
        String nameCheckResult = List.checkName(name);
        if (!nameCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, nameCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            Optional<List> editResult = listService.editList(UUID.fromString(id), name);
            if (editResult.isPresent()) {
                return new ResponseEntity<>(editResult.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "List not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listDelete(@RequestBody ObjectNode obj) {
        if (!obj.has("id")) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter id not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String id = obj.get("id").asText();
        String idCheckResult = List.checkId(id);
        if (!idCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, idCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            if (listService.deleteList(UUID.fromString(id))) {
                return new ResponseEntity<>(new ApiResponse(true, "List deleted"), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "List not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}