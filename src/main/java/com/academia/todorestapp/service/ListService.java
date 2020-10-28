package com.academia.todorestapp.service;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.payloads.GetListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

public interface ListService {

    List addList(List list);

    GetListResponse getAll(Pageable pageable);

    GetListResponse getAllWithSpec(Specification<List> spec, Pageable pageable);

    Optional<List> editList(UUID id, String name);

    boolean deleteList(UUID id);

    void checkIfListShouldBeDone(List list, UUID excludeCheckTaskId);

    void saveList(List list);
}
