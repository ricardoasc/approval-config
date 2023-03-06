package io.sicredi.repository;

import io.sicredi.dto.ConfigCreateDTO;
import io.sicredi.entity.Config;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ConfigRepository extends ReactiveMongoRepository<Config, String> {
    Flux<ConfigCreateDTO> findAllByOrderByRuleNumberDesc();
}
