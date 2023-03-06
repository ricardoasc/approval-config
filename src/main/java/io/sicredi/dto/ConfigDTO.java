package io.sicredi.dto;

import io.sicredi.models.Task;
import io.sicredi.enums.ConfigSituation;
import io.sicredi.enums.ProposalChannel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@With
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigDTO {
    private String id;
    private Long ruleNumber;
    private String cooperativeCode;
    private List<String> agencies;
    private List<String> products;
    private List<Task> tasks;
    private String productType;
    private ConfigSituation situation;
    private List<ProposalChannel> proposalChannel;
    private LocalDateTime lastChanged;
    private LocalDateTime createdAt;
    private String lastChangeByUser;
    private String createdByUser;
    private String changedByUser;
}
