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
    private boolean done;

    public Task() {
    }

    public Task(String name, UUID listId) {
        this.name = name;
        this.listId = listId;
    }

    public static String checkName(String name) {
        int nameLength = name.length();
        if (nameLength < 1 || nameLength > 50) return "Bad length of parameter name";
        return "ok";
    }

    public static String checkId(long id) {
        if (id < 1) return "Parameter id cant be less 1";
        return "ok";
    }
}
