package io.sicredi.dto;

import io.sicredi.enums.TaskType;
import io.sicredi.models.User;
import lombok.*;

import java.util.List;

@With
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private String id;
    private TaskType type;
    private Integer orderExecution;
    private boolean connectedPerson;
    private boolean statutoryMember;
    private boolean completed;
    private Number maxRatePercent;
    private Number minRatePercent;
    private List<User> users;
}
