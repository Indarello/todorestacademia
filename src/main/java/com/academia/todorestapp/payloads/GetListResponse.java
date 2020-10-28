package com.academia.todorestapp.payloads;

import com.academia.todorestapp.entities.List;
import lombok.Data;
import org.springframework.data.domain.Page;

/**
 * Класс формирует json-объект, который требуется для вывода списка List.
 */
@Data
public class GetListResponse {

    private int done;

    private int notDone;

    Page<List> page;

    public GetListResponse(int done, int notDone, Page<List> page) {
        this.done = done;
        this.notDone = notDone;
        this.page = page;
    }
}