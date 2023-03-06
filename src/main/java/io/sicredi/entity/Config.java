package io.sicredi.entity;

import io.sicredi.enums.ConfigSituation;
import io.sicredi.enums.ProposalChannel;
import io.sicredi.models.Task;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Document
public class Config {
    @Id
    private String id;

    @Indexed
    private Long ruleNumber;

    @Indexed
    private String cooperativeCode;

    @Indexed
    private List<String> agencies;

    @Indexed
    private List<String> products;

    @Indexed
    private List<ProposalChannel> proposalChannel;

    private List<Task> tasks;

    private ConfigSituation situation;
    private String productType;
    private Boolean completed;
    private String concludedBy;
    private String createdByUsername;
    private String changedByUsername;
    private LocalDateTime lastChanged;
    private LocalDateTime createdAt;
    private String lastChangeByUser;
}