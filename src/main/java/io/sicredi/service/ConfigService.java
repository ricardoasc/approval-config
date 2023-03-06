package io.sicredi.service;

import com.amazonaws.util.CollectionUtils;
import io.sicredi.client.ProductCatalogClient;
import io.sicredi.client.dto.ProductParameterDTO;
import io.sicredi.converter.ConfigConverter;
import io.sicredi.converter.PageConverter;
import io.sicredi.dto.*;
import io.sicredi.entity.Config;
import io.sicredi.enums.ConfigSituation;
import io.sicredi.enums.ProposalChannel;
import io.sicredi.repository.ConfigRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.sicredi.error.ErrorDefinitionImpl.*;
import static io.sicredi.error.Exceptions.businessException;
import static io.sicredi.error.Exceptions.notFoundException;
import static reactor.core.publisher.Mono.error;
import static reactor.core.publisher.Mono.just;

@Service
@AllArgsConstructor
public class ConfigService {
    private final ConfigRepository configRepository;
    private final ReactiveMongoOperations reactiveMongoOperations;
    private final ProductCatalogClient productCatalogClient;

    public Mono<ConfigDTO> save(ConfigCreateDTO dto, String username, String name) {
        return getMaxIndexSequential()
                .map(maxSequence->dto.withRuleNumber(++maxSequence))
                .map(it -> it.withProposalChannel(List.of(ProposalChannel.AGENCY)))
                .map(it -> it.withCreatedByUser(username))
                .map(it -> it.withLastChanged(LocalDateTime.now()))
                .map(it -> it.withCreatedAt(LocalDateTime.now()))
                .map(it -> it.withLastChangeByUser(name))
                .map(it -> it.withSituation(ConfigSituation.IN_ANALYSIS))
                .map(ConfigConverter::convert)
                .filterWhen(this::isConfigDuplicated)
                .flatMap(configRepository::save)
                .map(ConfigConverter::convert);
    }

    public Mono<ConfigDTO> update(String id, ConfigUpdateDTO dto, String username, String name) {
        return configRepository.findById(id)
                .switchIfEmpty(error(notFoundException(IAC_CONFIG_NOT_FOUND_ERROR_MSG)))
                .map(config -> ConfigConverter.convert(config, dto))
                .map(it -> it.withProposalChannel(List.of(ProposalChannel.AGENCY)))
                .map(it -> it.withChangedByUsername(username))
                .map(it -> it.withLastChanged(LocalDateTime.now()))
                .map(it -> it.withLastChangeByUser(name))
                .map(it -> it.withId(id))
                .filterWhen(this::checkExistOneTaskWithRateValid)
                .flatMap(configRepository::save)
                .map(ConfigConverter::convert);
    }

    public Mono<ConfigDTO> inactive(String id) {
        return configRepository.findById(id)
                .switchIfEmpty(error(notFoundException(IAC_CONFIG_NOT_FOUND_ERROR_MSG)))
                .flatMap(this::inactiveOrPermanentlyDeleteConfig);
    }

    public Mono<ConfigDTO> getById(String id) {
        return configRepository.findById(id)
                .switchIfEmpty(error(notFoundException(IAC_CONFIG_NOT_FOUND_ERROR_MSG)))
                .map(ConfigConverter::convert);
    }

    public Mono<PageDTO> findAll(ConfigRequestDTO reqFilters, Integer page, Integer pageSize) {
        return countWithoutPageable(reqFilters)
                .zipWith(findAllByFilters(reqFilters, true))
                .map(t -> PageConverter.convert(t.getT1(), page, pageSize,
                        reqFilters.getPageable().get().getSort(),
                        null, t.getT2()));
    }

    private Mono<ConfigDTO> inactiveOrPermanentlyDeleteConfig(Config config) {

        if (config.getSituation().equals(ConfigSituation.IN_ANALYSIS)) {
            return configRepository
                    .deleteById(config.getId())
                    .map(v -> ConfigConverter.convert(config));
        }

        config.setSituation(ConfigSituation.CLOSED);
        return configRepository
                .save(config)
                .map(ConfigConverter::convert);
    }

    private Mono<Boolean> isConfigDuplicated(Config config) {
        return just(ConfigConverter
                .convert(config, ConfigSituation.IN_FORCE))
                .flatMap(filters -> findAllByFilters(filters, false))
                .filter(List::isEmpty)
                .switchIfEmpty(error(businessException(IAC_CONFIG_DUPLICATED_MSG)))
                .map(v -> true);
    }

    private Mono<Long> countWithoutPageable(ConfigRequestDTO reqFilters) {
        Query qry = createQueryWithFilters(reqFilters, false);
        return reactiveMongoOperations.count(qry, Config.class);
    }

    private Mono<List<ConfigDTO>> findAllByFilters(ConfigRequestDTO reqFilters, Boolean pageable) {
        Query qry = createQueryWithFilters(reqFilters, pageable);
        return reactiveMongoOperations
                .find(qry, Config.class)
                .map(ConfigConverter::convert)
                .collectList();
    }
    private Query createQueryWithFilters(ConfigRequestDTO reqFilters, boolean pageable) {
        Criteria criteria = new Criteria();
        Query query = new Query();

        if (reqFilters.getCooperativeCode().isPresent()) {
            query.addCriteria(criteria.and("cooperativeCode")
                    .regex(reqFilters.getCooperativeCode().get()));
        }

        if (reqFilters.getSituation().isPresent()) {
            query.addCriteria(criteria.and("situation")
                    .regex(String.valueOf(reqFilters.getSituation().get())));
        }

        if (reqFilters.getRuleNumber().isPresent()) {
            query.addCriteria(criteria.and("ruleNumber").in(reqFilters.getRuleNumber().get()));
        }

        if (reqFilters.getProductType().isPresent()) {
            query.addCriteria(criteria.and("productType")
                    .regex(reqFilters.getProductType().get()));
        }

        if (reqFilters.getProducts().isPresent()) {
            query.addCriteria(criteria.and("products").in(reqFilters.getProducts().get()));
        }

        if (reqFilters.getAgencies().isPresent()) {
            query.addCriteria(criteria.and("agencies").in(reqFilters.getAgencies().get()));
        }

        if (reqFilters.getPageable().isPresent() && pageable) {
            query.with(reqFilters.getPageable().get());
        }

        return query;
    }

    private Mono<Boolean> checkExistOneTaskWithRateValid(Config config) {
        if (ConfigSituation.IN_ANALYSIS.equals(config.getSituation())) return Mono.just(true);
        return getFirstProduct(config)
                .flatMap(firstProduct -> productCatalogClient
                            .getParametersProduct(firstProduct,
                                    config.getCooperativeCode()))
                .filter(v -> !CollectionUtils.isNullOrEmpty(config.getTasks()))
                .filter(product -> getTaskWithRateValid(config, product))
                .switchIfEmpty(error(businessException(IAC_CONFIG_TASK_INVALID_RATE_MSG)))
                .map(v -> true);
    }

    private Boolean getTaskWithRateValid(Config config, ProductParameterDTO product) {
        return config.getTasks().stream().anyMatch(task ->
                        task.getMaxRatePercent().doubleValue() ==
                                product.getMaxProductRate().doubleValue());
    }

    private Mono<String> getFirstProduct(Config config) {
        return just(config)
                .filter(v -> !CollectionUtils.isNullOrEmpty(v.getProducts()))
                .map(Config::getProducts)
                .flatMap(products -> just(products.stream().findFirst()))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Mono<Long> getMaxIndexSequential() {
        return configRepository.findAllByOrderByRuleNumberDesc()
                .next()
                .defaultIfEmpty(ConfigCreateDTO.builder().ruleNumber(0L).build())
                .map(ConfigCreateDTO::getRuleNumber);
    }

}
