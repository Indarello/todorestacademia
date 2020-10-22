package com.academia.todorestapp.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.repository.ListRepository;
import com.academia.todorestapp.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private ListRepository listRepository;

    @Override
    public List addList(List list) {
        return listRepository.saveAndFlush(list);
    }

    @Override
    public java.util.List<List> getAll() {
        return listRepository.findAll();
    }

    @Override
    public Optional<List> editList(UUID id, String name) {
        Optional<List> searchResult = listRepository.findById(id);
        if (searchResult.isPresent()) {
            List foundedList = searchResult.get();
            foundedList.setName(name);
            foundedList.setEditDate(Timestamp.valueOf(LocalDateTime.now()));

            return Optional.of(listRepository.saveAndFlush(foundedList));
        }

        return Optional.empty();
    }

    @Override
    public boolean deleteList(UUID id) {
        Optional<List> searchResult = listRepository.findById(id);
        if (searchResult.isPresent()) {
            listRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
