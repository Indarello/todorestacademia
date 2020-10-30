package com.academia.todorestapp.util;

import com.academia.todorestapp.entities.Task;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Сборщик спецификация для сущности Task
 * Определяет какую операцию надо совершить в зависимости от содержимого String key, String operation
 * key - это имя параметра в сущности, например name
 * operation это операция - например Contains, т.е. имя должно содержить
 * value - это значение - например "написать", т.е. имя должно содержать "написать"
 * Необходима для фильтрации, например вывести Task, имя которых содержит "написать"
 */
public final class TaskSpecificationsBuilder {

    private java.util.List<SearchCriteria> params;

    public TaskSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public TaskSpecificationsBuilder with(String key, String operation, String value, String prefix, String suffix) {

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
            System.out.println("T[" + key + "]" + "[" + value + "]");
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
                case "description":
                case "urgency":
                    params.add(new SearchCriteria(key, op, value));
                    break;
            }
        }
        return this;
    }

    public Specification<Task> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification result = new TaskSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new TaskSpecification(params.get(i)));
        }

        return result;
    }
}
