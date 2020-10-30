package com.academia.todorestapp.entities;

import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность задачи
 */
@Data
@Entity
public class Task {

    /**
     * id задачи
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * id списка к кторому задача принадлежит
     */
    @NotNull
    private UUID listId;

    /**
     * Дата создания задачи
     */
    @NotNull
    private Timestamp createDate;

    /**
     * Дата последнего изменения задачи
     */
    @NotNull
    private Timestamp editDate;

    /**
     * Имя задачи
     */
    @NotNull
    private String name;

    /**
     * Описание задачи
     */
    @NotNull
    private String description;

    /**
     * Срочность задачи 1-5 (5 - максимальная срочность)
     */
    @NotNull
    private int urgency;

    /**
     * Статус завершенности задачи, является классом так как является не обязательным параметром при изменении
     * в REST методе Put "/task" и может принимать значение null там
     */
    @NotNull
    private Boolean done;

    public Task() {
    }

    /**
     * Создание новой задачи, новая задача всегда считается не завершенной
     * Дата создания и изменения ставится автоматически
     *
     * @param name        - имя задачи
     * @param listId      - id списка к кторому задача принадлежит
     * @param description - Описание задачи
     * @param urgency     - Срочность задачи 1-5 (5 - максимальная срочность)
     */
    public Task(String name, UUID listId, String description, int urgency) {
        this.name = name;
        this.listId = listId;
        this.createDate = Timestamp.valueOf(LocalDateTime.now());
        this.editDate = createDate;
        this.description = description;
        this.urgency = urgency;
        this.done = false;
    }

    /**
     * Проверяет правильность имени
     *
     * @param name - имя для проверки
     * @return String с результатом проверки
     */
    public static String checkName(String name) {
        int nameLength = name.length();
        if (nameLength < 1 || nameLength > 50) return "Bad length of parameter name";
        return "ok";
    }

    /**
     * Проверяет параметр срочности
     *
     * @param urgency - int значения срочности
     * @return String с результатом проверки
     */
    public static String checkUrgency(int urgency) {
        if (urgency < 1 || urgency > 5) return "Parameter urgency can be only 1-5";
        return "ok";
    }

    /**
     * Проверяет правильность описания
     *
     * @param description - String для проверки
     * @return String с результатом проверки
     */
    public static String checkDescription(String description) {
        int descriptionLength = description.length();
        if (descriptionLength > 200) return "Bad length of parameter description";
        return "ok";
    }
}
