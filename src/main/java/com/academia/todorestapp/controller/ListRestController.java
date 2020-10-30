package com.academia.todorestapp.controller;

import com.academia.todorestapp.dto.Dto;
import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.payloads.ApiResponse;
import com.academia.todorestapp.service.ListService;
import com.academia.todorestapp.util.ListSpecificationsBuilder;
import com.academia.todorestapp.util.SearchOperation;
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
public class ListRestController {

    private final ListService listService;

    @Autowired
    public ListRestController(ListService listService) {
        this.listService = listService;
    }

    /**
     * Добавление нового List
     *
     * @param list - сущность списка
     * @return ResponseEntity со статусом
     */
    @PostMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listAdd(@RequestBody List list) {
        String objName = list.getName();
        if (objName == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter name not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String nameCheckResult = List.checkName(objName);
        if (!nameCheckResult.equals("ok")) return new ResponseEntity<>(new ApiResponse(false, nameCheckResult), HttpStatus.NOT_ACCEPTABLE);

        List newList = new List(list.getName());

        try {
            return new ResponseEntity<>(listService.addList(newList), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Получение списков List, с пагинацией и дополнительной инфомрацией
     *
     * @param dto - объекст с разными параметрами
     * @return ResponseEntity со статусом
     */
    @GetMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listGetLists(@RequestBody Dto dto) {
        ListSpecificationsBuilder builder = new ListSpecificationsBuilder();
        Pageable pageable;
        String checkResult;

        if (!(checkResult = dto.checkNumberOfElements()).equals("ok") || !(checkResult = dto.checkRequestPage()).equals("ok")
                || !(checkResult = dto.checkSortParameterList()).equals("ok") || !(checkResult = dto.checkSortType()).equals("ok")
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

            Specification<List> spec = builder.build();

            return new ResponseEntity<>(listService.getAllWithSpec(spec, pageable), HttpStatus.OK);
        }

        try {
            return new ResponseEntity<>(listService.getAll(pageable), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Изменения параметров сущности List
     *
     * @param list - сущность списка
     * @return ResponseEntity со статусом
     */
    @PutMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listEdit(@RequestBody List list) {
        String name = list.getName();
        if (list.getId() == null || name == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter id|name not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String checkResult;
        if (!(checkResult = List.checkName(name)).equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, checkResult), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            Optional<List> editResult = listService.editList(list.getId(), name);
            if (editResult.isPresent()) {
                return new ResponseEntity<>(editResult.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "List not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Удаление сущности List
     *
     * @param id - id сущности
     * @return ResponseEntity со статусом
     */
    @DeleteMapping(value = "/list/{id}", produces = "application/json")
    public ResponseEntity<Object> listDelete(@PathVariable(name = "id") UUID id) {
        if (id == null) return new ResponseEntity<>(new ApiResponse(false, "Parameter id not provided"), HttpStatus.NOT_ACCEPTABLE);

        try {
            if (listService.deleteList(id)) {
                return new ResponseEntity<>(new ApiResponse(true, "List deleted"), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "List not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}