package com.academia.todorestapp.entities;

import com.sun.istack.NotNull;
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
