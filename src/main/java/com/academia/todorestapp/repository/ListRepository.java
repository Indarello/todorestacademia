package com.academia.todorestapp.repository;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Интерфейс от JPA для работы в бд с таблицей List
 */
@Repository
public interface ListRepository extends JpaRepository<List, UUID>, JpaSpecificationExecutor<List> {

}

