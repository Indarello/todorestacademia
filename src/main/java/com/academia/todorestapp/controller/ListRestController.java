package com.academia.todorestapp.controller;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.payloads.ApiResponse;
import com.academia.todorestapp.service.ListService;
import com.academia.todorestapp.util.ListSpecificationsBuilder;
import com.academia.todorestapp.util.SearchOperation;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        if (!nameCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, nameCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

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
     * @param obj - объекст с разными параметрами
     * @return ResponseEntity со статусом
     */
    @GetMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listGetLists(@RequestBody ObjectNode obj) {
        ListSpecificationsBuilder builder = new ListSpecificationsBuilder();
        int requestPage = 0;
        int numberOfElements = 10;
        String SortParameter = "createDate";
        String SortType = "ascending";
        Pageable pageable;

        if (obj.has("requestPage")) {
            requestPage = obj.get("requestPage").asInt();
            if (requestPage < 0 || requestPage > 100000) {
                return new ResponseEntity<>(new ApiResponse(false, "Parameter requestPage can be only 0-100000"), HttpStatus.NOT_ACCEPTABLE);
            }
        }

        if (obj.has("numberOfElements")) {
            numberOfElements = obj.get("numberOfElements").asInt();
            if (numberOfElements < 1 || numberOfElements > 100) {
                numberOfElements = 10;
            }
        }

        if (obj.has("SortParameter")) {
            SortParameter = obj.get("SortParameter").asText();
            if (!(SortParameter.equals("id") || SortParameter.equals("name") || SortParameter.equals("createDate") || SortParameter.equals("editDate") ||
                    SortParameter.equals("done"))) {
                return new ResponseEntity<>(
                        new ApiResponse(false, "Bad SortParameter, it can be only id|name|createDate|editDate|done"), HttpStatus.NOT_ACCEPTABLE);
            }
        }

        if (obj.has("SortType")) {
            SortType = obj.get("SortType").asText();
            if (!(SortType.equals("ascending") || SortType.equals("descending"))) {
                return new ResponseEntity<>(new ApiResponse(false, "Bad SortType, it can be only ascending|descending"), HttpStatus.NOT_ACCEPTABLE);
            }
        }

        if (SortType.equals("ascending")) {
            pageable = PageRequest.of(requestPage, numberOfElements, Sort.by(SortParameter));
        } else {
            pageable = PageRequest.of(requestPage, numberOfElements, Sort.by(SortParameter).descending());
        }

        if (obj.has("filter")) {
            String filter = obj.get("filter").asText();

            Pattern pattern =
                    Pattern.compile("([A-Za-z0-9_а-яА-Я]{2,})(" + SearchOperation.SIMPLE_OPERATION_SET + ")(\\*?)([A-Za-z0-9_а-яА-Я:\\-.+\\s]+?)(\\*?),", Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(filter + ",");
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
     * @param obj - объекст с разными параметрами
     * @return ResponseEntity со статусом
     */
    @PutMapping(value = "/list", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> listEdit(@RequestBody ObjectNode obj) {
        if (!obj.has("id") || !obj.has("name")) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter id or name not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String id = obj.get("id").asText();
        String idCheckResult = List.checkStringId(id);
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

    /**
     * Удаление сущности List
     *
     * @param id - id сущности
     * @return ResponseEntity со статусом
     */
    @DeleteMapping(value = "/list/{id}", produces = "application/json")
    public ResponseEntity<Object> listDelete(@PathVariable(name = "id") String id) {
        String idCheckResult = List.checkStringId(id);
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