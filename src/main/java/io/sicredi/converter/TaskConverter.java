package io.sicredi.converter;

import io.sicredi.dto.TaskCreateDTO;
import io.sicredi.dto.TaskDTO;
import io.sicredi.dto.TaskUpdateDTO;
import io.sicredi.models.Task;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TaskConverter {

    public Task convert(TaskCreateDTO source) {
        return Task.builder()
                .id(UUID.randomUUID().toString())
                .type(source.getType())
                .minRatePercent(source.getMinRatePercent())
                .maxRatePercent(source.getMaxRatePercent())
                .connectedPerson(source.isConnectedPerson())
                .statutoryMember(source.isStatutoryMember())
                .completed(source.isCompleted())
                .users(source.getUsers())
                .build();
    }

    public Task convert(TaskUpdateDTO source) {
        return Task.builder()
                .id(source.getId())
                .type(source.getType())
                .minRatePercent(source.getMinRatePercent())
                .maxRatePercent(source.getMaxRatePercent())
                .connectedPerson(source.isConnectedPerson())
                .statutoryMember(source.isStatutoryMember())
                .completed(source.isCompleted())
                .users(source.getUsers())
                .build();
    }

    public TaskDTO convert(Task source) {
        return TaskDTO.builder()
                .id(source.getId())
                .type(source.getType())
                .orderExecution(source.getOrderExecution())
                .minRatePercent(source.getMinRatePercent())
                .maxRatePercent(source.getMaxRatePercent())
                .connectedPerson(source.isConnectedPerson())
                .statutoryMember(source.isStatutoryMember())
                .completed(source.isCompleted())
                .users(source.getUsers())
                .build();
    }

    public Task convert(Task oldEntity, TaskUpdateDTO newDto) {
        return oldEntity.builder()
                .id(oldEntity.getId())
                .type(newDto.getType())
                .connectedPerson(newDto.isConnectedPerson())
                .statutoryMember(newDto.isStatutoryMember())
                .completed(newDto.isCompleted())
                .users(newDto.getUsers())
                .build();
    }
}
