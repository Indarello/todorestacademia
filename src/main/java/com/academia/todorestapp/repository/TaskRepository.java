package com.academia.todorestapp.repository;

import com.academia.todorestapp.entities.List;
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

    void deleteByListId(UUID id);

    java.util.List<Task> findAllByListId(UUID id);

    Page<Task> findAllByListId(Pageable pageable, UUID id);

    Page<Task> findAllByListId(Specification<Task> spec, Pageable pageable);
}