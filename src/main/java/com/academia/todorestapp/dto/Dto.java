package com.academia.todorestapp.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Сущность Dto для валидации входных параметров контроллера
 */
@Data
public class Dto {

    private UUID id;

    private UUID listId;

    private String name;

    private Timestamp createDate;

    private Timestamp editDate;

    private String description;

    private int urgency;

    private boolean done;

    private int requestPage = 0;

    private int numberOfElements = 10;

    String sortParameter = "createDate";

    String sortType = "ascending";

    String filter;

    /**
     * Проверяет параметр срочности
     *
     * @return String с результатом проверки
     */
    public String checkRequestPage() {
        if (this.requestPage < 0 || this.requestPage > 100000) return "Parameter requestPage can be only 0-100000";
        return "ok";
    }

    /**
     * Проверяет параметр необходимого колличества вывода элементов
     *
     * @return всегда "ok", т.к. не верные значения корректируются
     */
    public String checkNumberOfElements() {
        if (this.numberOfElements < 1 || this.numberOfElements > 100) this.numberOfElements = 10;
        return "ok";
    }

    /**
     * Проверяет параметр по которому ведется сортировка для сущности List
     *
     * @return String с результатом проверки
     */
    public String checkSortParameterList() {
        if (!(this.sortParameter.equals("id") || this.sortParameter.equals("name") || this.sortParameter.equals("createDate")
                || this.sortParameter.equals("editDate") || this.sortParameter.equals("done"))) {
            return "Bad SortParameter, it can be only id|name|createDate|editDate|done";
        }

        return "ok";
    }

    /**
     * Проверяет параметр по которому ведется сортировка для сущности Task
     *
     * @return String с результатом проверки
     */
    public String checkSortParameterTask() {
        if (!(this.sortParameter.equals("id") || this.sortParameter.equals("listId") || this.sortParameter.equals("name")
                || this.sortParameter.equals("createDate") || this.sortParameter.equals("editDate") || this.sortParameter.equals("description")
                || this.sortParameter.equals("urgency") || this.sortParameter.equals("done"))) {
            return "Bad SortParameter, it can be only id|listId|name|createDate|editDate|description|urgency|done";
        }

        return "ok";
    }

    /**
     * Проверяет тип сортировки
     *
     * @return String с результатом проверки
     */
    public String checkSortType() {
        if (!(this.sortType.equals("ascending") || this.sortType.equals("descending"))) return "Bad SortType, it can be only ascending|descending";

        return "ok";
    }

    /**
     * Проверяет параметр фильтрации, TODO: доработать валидацию
     *
     * @return String с результатом проверки
     */
    public String checkFilter() {
        int filterLength = filter.length();
        if (filterLength < 5 || filterLength > 100) return "Bad length of parameter filter";
        return "ok";
    }
}
