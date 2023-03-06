package io.sicredi.models;

import io.sicredi.enums.TaskType;
import lombok.*;

import java.util.List;

@Data
@With
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String id;
    private TaskType type;
    private Integer orderExecution;
    private Number minRatePercent;
    private Number maxRatePercent;
    private boolean connectedPerson;
    private boolean statutoryMember;
    private boolean completed;
    private List<User> users;
}