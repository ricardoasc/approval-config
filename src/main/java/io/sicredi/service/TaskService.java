package io.sicredi.service;

import com.amazonaws.util.CollectionUtils;
import io.sicredi.client.ProductCatalogClient;
import io.sicredi.client.dto.ProductParameterDTO;
import io.sicredi.converter.ConfigConverter;
import io.sicredi.converter.TaskConverter;
import io.sicredi.dto.ConfigDTO;
import io.sicredi.dto.TaskDTO;
import io.sicredi.dto.TaskCreateDTO;
import io.sicredi.dto.TaskUpdateDTO;
import io.sicredi.entity.Config;
import io.sicredi.models.Task;
import io.sicredi.models.User;
import io.sicredi.repository.ConfigRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.sicredi.error.ErrorDefinitionImpl.*;
import static io.sicredi.error.Exceptions.businessException;
import static io.sicredi.error.Exceptions.notFoundException;
import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.just;

@Service
@AllArgsConstructor
public class TaskService {

    private ConfigRepository configRepository;
    private ProductCatalogClient productCatalogClient;
    private TaskConverter taskConverter;

    public Mono<TaskDTO> createNewTask(String configId, TaskCreateDTO dto, String username, String name) {
        Task newTask = taskConverter.convert(dto);
        return configRepository
                .findById(configId)
                .switchIfEmpty(error(notFoundException(IAC_CONFIG_NOT_FOUND_ERROR_MSG)))
                .map(it -> it.withChangedByUsername(username))
                .map(v -> v.withLastChangeByUser(name))
                .map(v -> v.withLastChanged(LocalDateTime.now()))
                .map(v -> v.withTasks(Optional.ofNullable(v.getTasks())
                        .orElse(new ArrayList<>())))
                .filterWhen(config -> isValidExtraFee(config, taskConverter.convert(dto), false))
                .zipWhen(config -> just(dto)
                        .map(v -> config.getTasks().add(newTask))
                        .map(v -> config.getTasks()))
                .map(t -> t.getT1().withTasks(t.getT2()))
                .map(this::recalculatesOrderingTasks)
                .filterWhen(this::checkRangeRateTaskInvalid)
                .filterWhen(this::checkExistUsersDuplicatedInTasks)
                .flatMap(configRepository::save)
                .map(ConfigConverter::convert)
                .map(v -> taskConverter.convert(newTask));
    }

    public Mono<TaskDTO> updateTask(String configId, String taskId, TaskUpdateDTO dto, String username, String name) {
        return configRepository
                .findById(configId)
                .switchIfEmpty(error(notFoundException(IAC_CONFIG_NOT_FOUND_ERROR_MSG)))
                .map(it -> it.withChangedByUsername(username))
                .map(v -> v.withLastChangeByUser(name))
                .map(v -> v.withLastChanged(LocalDateTime.now()))
                .map(v -> v.withTasks(Optional.ofNullable(v.getTasks())
                        .orElse(new ArrayList<>())))
                .filter(config -> config.getTasks().stream().anyMatch(t -> Objects.equals(t.getId(), taskId)))
                .switchIfEmpty(error(notFoundException(IAC_TASK_NOT_FOUND_MSG)))
                .flatMap(config -> just(verifyAndUpdateTaskById(config, taskId, dto)))
                .map(this::recalculatesOrderingTasks)
                .filterWhen(this::checkRangeRateTaskInvalid)
                .filterWhen(this::checkExistUsersDuplicatedInTasks)
                .filterWhen(config -> isValidExtraFee(config, taskConverter.convert(dto), true))
                .flatMap(configRepository::save)
                .map(ConfigConverter::convert)
                .flatMap(v -> getTaskById(taskId, v.getTasks()))
                .map(v -> taskConverter.convert(v));
    }

    public Mono<ConfigDTO> removeTask(String configId, String taskId) {
        return configRepository
                .findById(configId)
                .switchIfEmpty(error(notFoundException(IAC_CONFIG_NOT_FOUND_ERROR_MSG)))
                .map(v -> v.withTasks(Optional.ofNullable(v.getTasks())
                        .orElse(new ArrayList<>())))
                .filter(config -> config.getTasks().stream().anyMatch(t -> Objects.equals(t.getId(), taskId)))
                .switchIfEmpty(error(notFoundException(IAC_TASK_NOT_FOUND_MSG)))
                .flatMap(config -> just(verifyAndRemoveTaskById(config, taskId)))
                .map(this::recalculatesOrderingTasks)
                .flatMap(configRepository::save)
                .map(ConfigConverter::convert);
    }

    private Mono<Task> getTaskById(String id, List<Task> tasks)  {
        return just(tasks.stream().filter(task ->
                task.getId().equals(id)).findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Config verifyAndRemoveTaskById(Config config, String taskId) {
        Optional<Task> taskFound = config.getTasks()
                .stream().filter(v -> v.getId().equals(taskId))
                .findFirst();
        taskFound.ifPresent(task -> config.getTasks().remove(task));
        return config;
    }

    private Config verifyAndUpdateTaskById(Config config, String taskId, TaskUpdateDTO dto) {
        Optional<Task> taskFound = config.getTasks()
                .stream().filter(v -> v.getId().equals(taskId))
                .findFirst();

        if (taskFound.isPresent()) {
            int index = config.getTasks().indexOf(taskFound.get());
            Task task = config.getTasks().get(index);
            Task newTask = taskConverter.convert(task, dto);

            Optional<Task> lastTask = config.getTasks().stream()
                    .skip(config.getTasks().size() - 1)
                    .findFirst();

            if (lastTask.isPresent() && taskId.equals(lastTask.get().getId())) {
                newTask.setMinRatePercent(dto.getMinRatePercent());
                newTask.setMaxRatePercent(dto.getMaxRatePercent());
            } else {
                newTask.setMinRatePercent(task.getMinRatePercent());
                newTask.setMaxRatePercent(task.getMaxRatePercent());
            }

            config.getTasks().set(index, newTask);
        }

        return config;
    }

    private Mono<Boolean> isValidExtraFee(Config config, Task task, Boolean isUpdate) {
        return just(config)
                .map(v -> v.withTasks(Optional.ofNullable(v.getTasks())
                        .orElse(new ArrayList<>())))
                .zipWhen(v -> productCatalogClient
                        .getParametersProduct(
                                v.getProducts().get(0),
                                v.getCooperativeCode()))
                .filterWhen(v -> isInvalidRateTaskAfterLast(task, v.getT1(), isUpdate))
                .filterWhen(v -> isValidTaskRatePercent(task, v.getT2()))
                .switchIfEmpty(error(businessException(IAC_INVALID_MAXIMUM_PROFITABILITY_MSG)))
                .map(v -> true);
    }

    private Mono<Boolean> isValidTaskRatePercent(Task task, ProductParameterDTO product) {
        return just(
                task.getMinRatePercent().doubleValue() >
                        product.getMaxRate().doubleValue() &&
                task.getMaxRatePercent().doubleValue() <=
                        product.getMaxProductRate().doubleValue());
    }

    private Mono<Boolean> isInvalidRateTaskAfterLast(Task task, Config config, Boolean isUpdate) {

        if (config.getTasks().isEmpty()) {
            return just(true);
        }

        Optional<Task> taskBeValidated = config
                .getTasks()
                .stream()
                .skip(config.getTasks().size() - 1)
                .findFirst();

        if ((isUpdate && taskBeValidated.isPresent() && !task.getId()
                .equals(taskBeValidated.get().getId())) ||
                isUpdate && config.getTasks().size() == 1) {
            return just(true);
        }

        if (isUpdate && task.getId().equals(taskBeValidated.get().getId())) {
            Integer lastTaskOrder = taskBeValidated.get().getOrderExecution() - 1;
            taskBeValidated = config
                    .getTasks()
                    .stream()
                    .filter(v -> v.getOrderExecution().equals(lastTaskOrder))
                    .findFirst();
        }

        return just(taskBeValidated)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(v -> v.getMinRatePercent().doubleValue() <=
                                task.getMinRatePercent().doubleValue())
                .filter(v -> v.getMaxRatePercent().doubleValue() <=
                                task.getMaxRatePercent().doubleValue())
                .switchIfEmpty(error(businessException(IAC_TASK_INVALID_MIN_MAX_RATE_MSG)))
                .map(v -> true);
    }

    private Mono<Boolean> checkRangeRateTaskInvalid(Config config) {

        if (config.getTasks().isEmpty()) {
            return just(true);
        }

        Optional<Task> taskInvalidExist = config
                .getTasks()
                .stream()
                .filter(v ->
                v.getMaxRatePercent().doubleValue() <
                v.getMinRatePercent().doubleValue())
                .findFirst();

        return just(taskInvalidExist)
                .filter(Optional::isEmpty)
                .switchIfEmpty(error(businessException(IAC_TASK_INVALID_MAX_RATE_MSG)))
                .map(v -> true);
    }

    private Mono<Boolean> checkExistUsersDuplicatedInTasks(Config config) {
        return just(config.getTasks().stream()
                .filter(v -> !CollectionUtils.isNullOrEmpty(v.getUsers()))
                .flatMap(task -> task.getUsers().stream().map(User::getLdap)))
                .map(v -> v.collect(Collectors.toList()))
                .filter(v ->  !(v.size() > v.stream().distinct().count()))
                .switchIfEmpty(error(businessException(IAC_TASK_WITH_DUPLICATED_USER)))
                .map(v -> true);
    }


    private Config recalculatesOrderingTasks(Config config) {

        if (config.getTasks().isEmpty()) {
            return config;
        }

        config.getTasks().stream()
                .findFirst()
                .ifPresent(task -> task.setOrderExecution(1));

        for (int index = 1; index < config.getTasks().size(); index++) {
            config.getTasks().get(index).setOrderExecution(index + 1);
        }

        return config;
    }
}
