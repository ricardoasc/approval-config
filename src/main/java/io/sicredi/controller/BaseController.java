package io.sicredi.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public abstract class BaseController {
    public Pageable getPageable(Integer page, Integer size, Sort.Direction sortDirection, String sortBy) {
        return PageRequest.of(Optional.ofNullable(page).orElse(0),
                Optional.ofNullable(size).orElse(10),
                Optional.ofNullable(sortDirection).orElse(Sort.Direction.ASC),
                Optional.ofNullable(sortBy).orElse("id"));
    }
}