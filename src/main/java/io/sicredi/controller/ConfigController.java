package io.sicredi.controller;

import io.sicredi.dto.*;
import io.sicredi.service.ConfigService;
import io.sicredi.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "config")
@AllArgsConstructor
public class ConfigController extends BaseController {

    private final ConfigService configService;
    private final TaskService taskService;

    @GetMapping
    private Mono<PageDTO> findAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") final Integer pageSize,
            @RequestParam(name = "sortDirection", required = false) final Sort.Direction sortDirection,
            @RequestParam(name = "sortBy", required = false) final String sortBy,
            final ConfigRequestDTO reqFilters) {
        reqFilters.setPageable(Optional.ofNullable(getPageable(page, pageSize, sortDirection, sortBy)));
        return configService.findAll(reqFilters, page, pageSize);
    }

    @GetMapping("/{id}")
    private Mono<ConfigDTO> getConfigById(@PathVariable(value = "id") String id) {
        return configService.getById(id);
    }

    @PostMapping
    private Mono<ConfigDTO> postConfig(
            @Valid @RequestBody final ConfigCreateDTO dto,
            @RequestHeader(name = "username") final String username,
            @RequestHeader(name = "name") final String name) {
        return configService.save(dto, username, name);
    }

    @PutMapping("/{id}")
    private Mono<ConfigDTO> putConfig(
            @PathVariable(value = "id") String id,
            @Valid @RequestBody final ConfigUpdateDTO dto,
            @RequestHeader(name = "username") final String username,
            @RequestHeader(name = "name") final String name) {
        return configService.update(id ,dto, username, name);
    }

    @DeleteMapping("/{id}")
    private Mono<ConfigDTO> inactiveConfig(@PathVariable(value = "id") String id) {
        return configService.inactive(id);
    }

    @PostMapping("/{configId}/tasks")
    private Mono<TaskDTO> addTask(
            @PathVariable("configId") final String configId,
            @Valid @RequestBody final TaskCreateDTO dto,
            @RequestHeader(name = "username") final String username,
            @RequestHeader(name = "name") final String name) {
        return taskService.createNewTask(configId, dto, username, name);
    }

    @PutMapping("/{configId}/tasks/{taskId}")
    private Mono<TaskDTO> updateTask(
            @PathVariable("configId") final String configId,
            @PathVariable("taskId") final String taskId,
            @Valid @RequestBody final TaskUpdateDTO dto,
            @RequestHeader(name = "username") final String username,
            @RequestHeader(name = "name") final String name){
        return taskService.updateTask(configId, taskId, dto, username, name);
    }

    @DeleteMapping("/{configId}/tasks/{taskId}")
    private Mono<ConfigDTO> removeTask(
            @PathVariable("configId") final String configId,
            @PathVariable("taskId") final String taskId) {
        return taskService.removeTask(configId, taskId);
    }
}
