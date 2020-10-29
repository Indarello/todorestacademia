package com.academia.todorestapp.entities;

import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

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

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    private UUID listId;

    @NotNull
    private Timestamp createDate;

    @NotNull
    private Timestamp editDate;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private int urgency;

    @NotNull
    private boolean done;

    public Task() {
    }

    public Task(String name, UUID listId, String description, int urgency) {
        this.name = name;
        this.listId = listId;
        this.createDate = Timestamp.valueOf(LocalDateTime.now());
        this.editDate = createDate;
        this.description = description;
        this.urgency = urgency;
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

    /**
     * Проверяет правильность id
     *
     * @param id - String UUID для проверки
     * @return String с результатом проверки
     */
    public static String checkStringId(String id) {
        //пока что полностью не изучил границы UUID, првоерка может поменяться в будущем, он всегда 36 символов?
        int idLength = id.length();
        if (idLength < 10 || idLength > 50) return "Bad length of parameter id";
        return "ok";
    }
}
