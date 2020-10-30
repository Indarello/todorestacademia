package com.academia.todorestapp.controller;

import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.payloads.ApiResponse;
import com.academia.todorestapp.service.TaskService;
import com.academia.todorestapp.util.SearchOperation;
import com.academia.todorestapp.util.TaskSpecificationsBuilder;
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
public class TaskRestController {

    private final TaskService taskService;

    @Autowired
    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Добавление нового Task
     *
     * @param task - сущность списка
     * @return ResponseEntity со статусом
     */
    @PostMapping(value = "/task", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> taskAdd(@RequestBody Task task) {
        String objName = task.getName();
        if (objName == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter name not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        String nameCheckResult = Task.checkName(objName);
        if (!nameCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, nameCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        int objUrgency = task.getUrgency();
        if (objUrgency == 0) {
            objUrgency = 1; //будет превращать некоторые некорректные значения в 0 и затем в 1, считается нормальным если не будет давать ошибку?
        } else {
            String objUrgencyCheckResult = Task.checkUrgency(objUrgency);
            if (!objUrgencyCheckResult.equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, objUrgencyCheckResult), HttpStatus.NOT_ACCEPTABLE);
            }
        }

        String objDescription = task.getDescription();
        if (objDescription == null) {
            objDescription = "";
        } else {
            String objDescriptionCheckResult = Task.checkDescription(objDescription);
            if (!objDescriptionCheckResult.equals("ok")) {
                return new ResponseEntity<>(new ApiResponse(false, objDescriptionCheckResult), HttpStatus.NOT_ACCEPTABLE);
            }
        }

        UUID objListId = task.getListId();
        if (objListId == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter listId not provided"), HttpStatus.NOT_ACCEPTABLE);
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
     * @param obj - объекст с разными параметрами
     * @return ResponseEntity со статусом
     */
    @GetMapping(value = "/task", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Object> taskGetTasks(@RequestBody ObjectNode obj) {
        TaskSpecificationsBuilder builder = new TaskSpecificationsBuilder();
        int requestPage = 0;
        int numberOfElements = 10;
        UUID listId;
        String SortParameter = "createDate";
        String SortType = "ascending";
        Pageable pageable;

        if (obj.has("listId")) {
            listId = UUID.fromString(obj.get("listId").asText()); //TODO валидация listId
        } else {
            return new ResponseEntity<>(new ApiResponse(false, "Parameter listId not provided"), HttpStatus.NOT_ACCEPTABLE);
        }

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
            if (!(SortParameter.equals("id") || SortParameter.equals("listId") || SortParameter.equals("name") || SortParameter.equals("createDate") ||
                    SortParameter.equals("editDate") || SortParameter.equals("description") || SortParameter.equals("urgency") ||
                    SortParameter.equals("done"))) {
                return new ResponseEntity<>(
                        new ApiResponse(false, "Bad SortParameter, it can be only id|listId|name|createDate|editDate|description|urgency|done"), HttpStatus.NOT_ACCEPTABLE);
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
                    Pattern.compile("([A-Za-z0-9_а-яА-Я]{2,})(" + SearchOperation.SIMPLE_OPERATION_SET + ")(\\*?)([A-Za-z0-9_а-яА-Я:\\-.+\\s]+?)(\\*?),",
                            Pattern.UNICODE_CHARACTER_CLASS
                    );
            Matcher matcher = pattern.matcher(filter + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
            }

            Specification<Task> spec = builder.build();

            //Specification<Task> spec1 = new TaskSpecification(new SearchCriteria("listId", SearchOperation.EQUALITY, listId));  //TODO: UUID в specification

            //return new ResponseEntity<>(taskService.getAllWithSpec(Specification.where(spec1).and(spec), pageable), HttpStatus.OK);
            return new ResponseEntity<>(taskService.getAllWithSpec(spec, pageable), HttpStatus.OK);
        }

        try {
            return new ResponseEntity<>(taskService.getAll(pageable, listId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Изменение сущности Task
     *
     * @param obj - объекст с разными параметрами
     * @return ResponseEntity со статусом
     */
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

        if (!name.isPresent() && !description.isPresent() && !done.isPresent() && !urgency.isPresent()) {
            return new ResponseEntity<>(new ApiResponse(false, "None of parameter name,description,urgency,done provided"), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            Optional<Task> editResult = taskService.editTask(UUID.fromString(id), name, description, urgency, done);

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
    public ResponseEntity<Object> taskMarkDone(@PathVariable(name = "id") String id) {
        String idCheckResult = Task.checkStringId(id);
        if (!idCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, idCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            Optional<Task> editResult = taskService.markDoneTask(UUID.fromString(id));

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
    public ResponseEntity<Object> taskDelete(@PathVariable(name = "id") String id) {
        String idCheckResult = Task.checkStringId(id);
        if (!idCheckResult.equals("ok")) {
            return new ResponseEntity<>(new ApiResponse(false, idCheckResult), HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            if (taskService.deleteTask(UUID.fromString(id))) {
                return new ResponseEntity<>(new ApiResponse(true, "Task deleted"), HttpStatus.OK);
            }

            return new ResponseEntity<>(new ApiResponse(false, "Task not found, or database error (List not found)"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}