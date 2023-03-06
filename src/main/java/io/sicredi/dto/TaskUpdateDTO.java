package io.sicredi.dto;

import io.sicredi.enums.TaskType;
import io.sicredi.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateDTO {
    @NotNull(message = "O id da tarefa deve ser informado")
    private String id;

    @NotNull(message = "O tipo da tarefa deve ser informado")
    private TaskType type;

    private boolean connectedPerson;
    private boolean statutoryMember;
    private boolean completed;

    @NotNull(message = "O atributo minRatePercent deve ser informado")
    private Number minRatePercent;

    @NotNull(message = "O atributo maxRatePercent deve ser informado")
    private Number maxRatePercent;

    private List<User> users;
}
