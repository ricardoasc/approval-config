package io.sicredi.converter;

import io.sicredi.dto.ConfigCreateDTO;
import io.sicredi.dto.ConfigDTO;
import io.sicredi.dto.ConfigRequestDTO;
import io.sicredi.dto.ConfigUpdateDTO;
import io.sicredi.entity.Config;
import io.sicredi.enums.ConfigSituation;

import java.util.Optional;

public class ConfigConverter {

    public static ConfigDTO convert(Config source) {
        return ConfigDTO.builder()
                .id(source.getId())
                .ruleNumber(source.getRuleNumber())
                .situation(source.getSituation()!=null?source.getSituation():ConfigSituation.IN_ANALYSIS)
                .cooperativeCode(source.getCooperativeCode())
                .productType(source.getProductType())
                .lastChanged(source.getLastChanged())
                .lastChangeByUser(source.getLastChangeByUser())
                .createdByUser(source.getCreatedByUsername())
                .changedByUser(source.getChangedByUsername())
                .tasks(source.getTasks())
                .agencies(source.getAgencies())
                .productType(source.getProductType())
                .products(source.getProducts())
                .proposalChannel(source.getProposalChannel())
                .build();
    }

    public static Config convert(Config oldEntity, ConfigUpdateDTO newDto) {
        return oldEntity.builder()
                .situation(newDto.getSituation()!=null?newDto.getSituation():ConfigSituation.IN_ANALYSIS)
                .cooperativeCode(newDto.getCooperativeCode())
                .lastChangeByUser(newDto.getLastChangeByUser())
                .changedByUsername(newDto.getChangedByUser())
                .productType(newDto.getProductType())
                .agencies(newDto.getAgencies())
                .products(newDto.getProducts())
                .lastChanged(newDto.getLastChanged())
                .ruleNumber(oldEntity.getRuleNumber())
                .createdByUsername(oldEntity.getCreatedByUsername())
                .tasks(oldEntity.getTasks())
                .build();
    }

    public static Config convert(ConfigCreateDTO dto) {
        return Config.builder()
                .ruleNumber(dto.getRuleNumber())
                .situation(dto.getSituation()!=null?dto.getSituation():ConfigSituation.IN_ANALYSIS)
                .lastChangeByUser(dto.getLastChangeByUser())
                .createdByUsername(dto.getCreatedByUser())
                .productType(dto.getProductType())
                .cooperativeCode(dto.getCooperativeCode())
                .agencies(dto.getAgencies())
                .products(dto.getProducts())
                .proposalChannel(dto.getProposalChannel())
                .lastChanged(dto.getLastChanged())
                .completed(false)
                .build();
    }

    public static ConfigRequestDTO convert(Config config, ConfigSituation situation) {
        ConfigRequestDTO filters = new ConfigRequestDTO();
        filters.setSituation(Optional.ofNullable(situation));
        filters.setAgencies(Optional.ofNullable(config.getAgencies()));
        filters.setProducts(Optional.ofNullable(config.getProducts()));
        filters.setProductType(Optional.ofNullable(config.getProductType()));
        filters.setPageable(Optional.empty());
        return filters;
    }
}
