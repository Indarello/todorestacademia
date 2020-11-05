package com.academia.todorestapp.entities;

import javax.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность списка заданий
 */
@Data
@Entity
public class List {

    /**
     * id списка
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
     * Имя списка
     */
    @NotNull
    private String name;

    /**
     * Дата создания списка
     */
    @NotNull
    private Timestamp createDate;

    /**
     * Дата последнего изменения списка
     */
    @NotNull
    private Timestamp editDate;

    /**
     * Статус завершенности списка, true если все дела в списке завершены
     */
    @NotNull
    private boolean done;

    public List() {
    }

    /**
     * Создание нового списка, пустой список всегда считается завершенным
     * Дата создания и изменения ставится автоматически
     *
     * @param name - имя нового списка
     */
    public List(String name) {
        this.createDate = Timestamp.valueOf(LocalDateTime.now());
        this.editDate = createDate;
        this.name = name;
        this.done = true;
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
}
