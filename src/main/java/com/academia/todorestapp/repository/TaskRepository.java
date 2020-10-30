package com.academia.todorestapp.repository;

import com.academia.todorestapp.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Интерфейс от JPA для работы в бд с таблицей Task
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    /**
     * Удаление всех сущностей Task принадлежащих List по id
     * @param id - id сущности List
     */
    void deleteByListId(UUID id);

    /**
     * Вывод всех сущностей Task принадлежащих List по id
     * @param id - id сущности List
     * @return Список Task
     */
    java.util.List<Task> findAllByListId(UUID id);

    /**
     * Вывод всех сущностей Task принадлежащих List по id
     * @param pageable - параметры страницы (сколько элементов на страницу, номер страницы и сортировка)
     * @param id - id сущности List
     * @return Страница со списком Task и информацией по пагинации
     */
    Page<Task> findAllByListId(Pageable pageable, UUID id);

}