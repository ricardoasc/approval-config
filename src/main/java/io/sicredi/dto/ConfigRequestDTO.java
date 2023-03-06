package io.sicredi.dto;

import io.sicredi.enums.ConfigSituation;
import lombok.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class ConfigRequestDTO {
    private Optional<Pageable> pageable = Optional.empty();
    private Optional<ConfigSituation> situation = Optional.empty();
    private Optional<List<String>> products = Optional.empty();
    private Optional<List<String>> agencies = Optional.empty();
    private Optional<List<Long>> ruleNumber = Optional.empty();
    private Optional<String> cooperativeCode = Optional.empty();
    private Optional<String> productType = Optional.empty();
}
