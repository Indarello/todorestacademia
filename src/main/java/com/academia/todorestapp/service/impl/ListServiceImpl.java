package com.academia.todorestapp.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.academia.todorestapp.entities.List;
import com.academia.todorestapp.entities.Task;
import com.academia.todorestapp.payloads.GetListResponse;
import com.academia.todorestapp.repository.ListRepository;
import com.academia.todorestapp.repository.TaskRepository;
import com.academia.todorestapp.service.ListService;
import com.academia.todorestapp.util.ListSpecification;
import com.academia.todorestapp.util.SearchCriteria;
import com.academia.todorestapp.util.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListServiceImpl implements ListService {

    private final TaskRepository taskRepository;

    private final ListRepository listRepository;

    @Autowired
    public ListServiceImpl(TaskRepository taskRepository, ListRepository listRepository) {
        this.taskRepository = taskRepository;
        this.listRepository = listRepository;
    }

    @Override
    @Transactional
    public List addList(List list) {
        return listRepository.saveAndFlush(list);
    }

    @Override
    public GetListResponse getAll(Pageable pageable) {
        Page<List> searchResult = listRepository.findAll(pageable);
        return getGetListResponse(searchResult);
    }

    @Override
    public GetListResponse getAllWithSpec(Specification<List> spec, Pageable pageable) {
        Page<List> searchResult = listRepository.findAll(spec, pageable);
        return getGetListResponse(searchResult);
    }

    private GetListResponse getGetListResponse(Page<List> searchResult) {
        java.util.List<List> listOfLists = searchResult.toList();
        int done = 0;
        int notDone = 0;
        for (List list : listOfLists) {
            if (list.isDone()) {
                done++;
            } else {
                notDone++;
            }
        }

        return new GetListResponse(done, notDone, searchResult);
    }

    @Override
    @Transactional
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
    @Transactional
    public boolean deleteList(UUID id) {
        Optional<List> searchResult = listRepository.findById(id);
        if (searchResult.isPresent()) {
            taskRepository.deleteByListId(id);
            listRepository.deleteById(id);
            return true;
        }

        return false;
    }

    /**
     * Проверяем стоит ли выставить списку статус сделан, например когда изменяем одно из task в списке или удаляем не сделанное task
     */
    @Override
    public void checkIfListShouldBeDone(List list, UUID excludeCheckTaskId) {
        if (!list.isDone()) {
            boolean checkResult = true;
            java.util.List<Task> ListOfTasks = taskRepository.findAllByListId(list.getId());

            for (Task task : ListOfTasks) {
                if (!task.isDone() && task.getId() != excludeCheckTaskId) {
                    checkResult = false;
                    break;
                }
            }

            if (checkResult) {
                list.setDone(true);
            }
        }
    }

    @Override
    @Transactional
    public void saveList(List list) {
        listRepository.save(list);
    }
}
