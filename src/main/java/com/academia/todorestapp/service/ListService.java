package com.academia.todorestapp.service;

import com.academia.todorestapp.entities.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

public interface ListService {

    List addList(List list);

    java.util.List<List> getAll();

    Optional<List> editList(UUID id, String name);

    boolean deleteList(UUID id);
}
