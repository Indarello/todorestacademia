package com.academia.todorestapp.payloads;

import com.academia.todorestapp.entities.Task;
import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * Класс формирует json-объект, который требуется для вывода списка Task.
 */
@Data
public class GetTaskResponse {

    private int done;

    private int notDone;

    Page<Task> page;

    public GetTaskResponse(int done, int notDone, Page<Task> page) {
        this.done = done;
        this.notDone = notDone;
        this.page = page;
    }
}