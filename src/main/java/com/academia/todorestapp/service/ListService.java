package com.academia.todorestapp.service;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.payloads.GetListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с сущностью List
 */
public interface ListService {

    /**
     * Добавление нового List
     *
     * @param list - сущность List
     * @return Сущность List со всеми параметрами сохраненными в БД
     */
    List addList(List list);

    /**
     * Поиск всех List
     *
     * @param pageable - параметры страницы (сколько элементов на страницу, номер страницы и сортировка)
     * @return Вспомогательную сущность со списком List, пагинацией и дополнительной инфомрацией
     */
    GetListResponse getAll(Pageable pageable);

    /**
     * Поиск всех List
     *
     * @param spec     - параметры фильтрации
     * @param pageable - параметры страницы (сколько элементов на страницу, номер страницы и сортировка)
     * @return Вспомогательную сущность со списком List, пагинацией и дополнительной инфомрацией
     */
    GetListResponse getAllWithSpec(Specification<List> spec, Pageable pageable);

    /**
     * Изменение сущности List
     *
     * @param id   - id List
     * @param name - новое имя
     * @return Сущность List со всеми параметрами сохраненными в БД
     */
    Optional<List> editList(UUID id, String name);

    /**
     * Удаление сущности List
     *
     * @param id - id List
     * @return boolean статус успешности выполнения
     */
    boolean deleteList(UUID id);

    /**
     * Проверяет надо ли выставить списку статус сделан, например когда удаляем не сделанное Task из этого List
     *
     * @param list               - сущность list
     * @param excludeCheckTaskId - id Task которое подверглость изменению, его исключаем из проверки
     */
    void checkIfListShouldBeDone(List list, UUID excludeCheckTaskId);

    /**
     * Сохранение сущности List в бд
     *
     * @param list - сущность list
     */
    void saveList(List list);
}
