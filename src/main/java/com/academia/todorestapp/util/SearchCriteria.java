package com.academia.todorestapp.util;

import lombok.Data;

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
