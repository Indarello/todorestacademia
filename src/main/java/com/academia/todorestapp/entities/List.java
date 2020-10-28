package com.academia.todorestapp.entities;

import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Сущность списка
 */
@Data
@Entity
public class List {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private Timestamp createDate;

    @NotNull
    private Timestamp editDate;

    @NotNull
    private boolean done;

    public List() {
    }

    public List(String name) {
        this.createDate = Timestamp.valueOf(LocalDateTime.now());
        this.editDate = createDate;
        this.name = name;
        this.done = true;
    }

    public static String checkName(String name) {
        int nameLength = name.length();
        if (nameLength < 1 || nameLength > 50) return "Bad length of parameter name";
        return "ok";
    }

    public static String checkStringId(String id) {
        //пока что полностью не изучил границы UUID, првоерка может поменяться в будущем, он всегда 36 символов?
        int idLength = id.length();
        if (idLength < 10 || idLength > 50) return "Bad length of parameter id";
        return "ok";
    }
}
