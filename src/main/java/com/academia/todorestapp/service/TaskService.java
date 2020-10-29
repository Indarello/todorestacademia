package com.academia.todorestapp.service;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.payloads.GetTaskResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface TaskService {

    /**
     * Добавление нового Task
     *
     * @param task - сущность Task
     * @return Сущность Task со всеми параметрами сохраненными в БД если удалось сохранить
     */
    Optional<Task> addTask(Task task);

    /**
     * Поиск List по id
     *
     * @param id - id List
     * @return Сущность List если удалось найти
     */
    Optional<List> FindListById(UUID id);

    /**
     * Поиск всех Task
     *
     * @param pageable - параметры страницы (сколько элементов на страницу, номер страницы и сортировка)
     * @param id       - id List к которому принадлежат
     * @return Вспомогательную сущность со списком Task, пагинацией и дополнительной инфомрацией
     */
    GetTaskResponse getAll(Pageable pageable, UUID id);

    /**
     * Поиск всех Task
     *
     * @param spec     - параметры фильтрации
     * @param pageable - параметры страницы (сколько элементов на страницу, номер страницы и сортировка)
     * @return Вспомогательную сущность со списком List, пагинацией и дополнительной инфомрацией
     */
    GetTaskResponse getAllWithSpec(Specification<Task> spec, Pageable pageable);

    /**
     * Изменение сущности Task
     *
     * @param id          - id сущности Task
     * @param name        - новое имя
     * @param description - новое описание
     * @param urgency     - новая срочность
     * @param done        - новый статус готовности
     * @return Сущность Task со всеми параметрами сохраненными в БД если удалось сохранить
     */
    Optional<Task> editTask(UUID id, Optional<String> name, Optional<String> description, Optional<Integer> urgency, Optional<Boolean> done);

    /**
     * Изменение статуса done для сущности Task
     *
     * @param id - id сущности Task
     * @return Сущность Task со всеми параметрами сохраненными в БД если удалось сохранить
     */
    Optional<Task> markDoneTask(UUID id);

    /**
     * Удаление сущности Task
     *
     * @param id - id Task
     * @return boolean статус успешности выполнения
     */
    boolean deleteTask(UUID id);
}
