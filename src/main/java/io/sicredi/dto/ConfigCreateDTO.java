package io.sicredi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sicredi.enums.ConfigSituation;
import io.sicredi.enums.ProposalChannel;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class ConfigCreateDTO {

    @NotEmpty(message = "O campo productType deve ser informado")
    private String productType;

    @NotEmpty(message = "O campo código da coperativa deve ser informado")
    private String cooperativeCode;

    @Size(min = 1, message = "Ao menos um produto deve ser informado")
    @NotEmpty(message = "O atributo products deve ser informado")
    private List<String> products;

    private List<String> agencies;

    @JsonIgnore()
    private List<ProposalChannel> proposalChannel;

    @JsonIgnore()
    private String createdByUser;

    @JsonIgnore()
    private LocalDateTime lastChanged;

    @JsonIgnore()
    private LocalDateTime createdAt;

    @JsonIgnore()
    private String lastChangeByUser;

    private ConfigSituation situation;
    private Long ruleNumber;
}
