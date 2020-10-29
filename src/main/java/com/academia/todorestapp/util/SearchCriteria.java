package com.academia.todorestapp.util;

import lombok.Data;

/**
 * Класс критерии поиска
 * key - это имя параметра в сущности, например name
 * operation это операция - например Contains, т.е. имя должно содержить
 * value - это значение - например "Четверг", т.е. имя должно содержать "Четверг"
 */
@Data
public class SearchCriteria {

    private String key;

    private SearchOperation operation;

    private Object value;

    public SearchCriteria() {

    }

    public SearchCriteria(final String key, final SearchOperation operation, final Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }
}
