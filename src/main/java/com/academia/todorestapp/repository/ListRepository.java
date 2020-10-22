package com.academia.todorestapp.repository;

import com.academia.todorestapp.entities.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Интерфейс от JPA для работы в бд с таблицей List
 */

@Repository
public interface ListRepository extends JpaRepository<List, UUID> {

}