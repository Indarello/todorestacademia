package com.academia.todorestapp.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.util.SearchOperation;
import com.academia.todorestapp.util.SearchCriteria;

public final class ListSpecificationsBuilder {

    private java.util.List<SearchCriteria> params;

    public ListSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public ListSpecificationsBuilder with(String key, String operation, String value, String prefix, String suffix) {

        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            if (op == SearchOperation.EQUALITY) {
                boolean startWithAsterisk = prefix.contains("*");
                boolean endWithAsterisk = suffix.contains("*");

                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                }
            }
            System.out.println("L[" + key + "]" + "[" + value + "]");
            switch (key) {
                case "id":
                    params.add(new SearchCriteria(key, op, UUID.fromString(value)));
                    break;
                case "createDate":                                          //TODO: фильтрация по датам не работает, валидация параметров фильтрации не сделана
                    Date date = Date.valueOf(value);
                    params.add(new SearchCriteria(key, op, date));
                    break;
                case "done":
                    if (value.equalsIgnoreCase("false")) {
                        params.add(new SearchCriteria(key, op, false));
                    } else {
                        params.add(new SearchCriteria(key, op, true));
                    }
                    break;
                case "name":
                    params.add(new SearchCriteria(key, op, value));
                    break;
            }
        }
        return this;
    }

    public Specification<List> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification result = new ListSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new ListSpecification(params.get(i)));
        }

        return result;
    }
}
