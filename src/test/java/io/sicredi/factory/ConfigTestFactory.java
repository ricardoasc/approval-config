package io.sicredi.factory;

import io.sicredi.client.dto.ProductParameterDTO;
import io.sicredi.dto.*;
import io.sicredi.enums.*;
import io.sicredi.models.Task;
import io.sicredi.models.User;

import java.util.ArrayList;
import java.util.List;

public final class ConfigTestFactory {

    public static final String CONFIG_ID_1 = "id-1";
    public static final String COOPERATIVE_CODE_1 = "0101";
    public static final String COOPERATIVE_CODE_2 = "0202";
    public static final Long RULE_NUMBER_1 = 999L;
    public static final List<String> AGENCIES_1 = List.of("AGENCY1_ID");
    public static final List<String> AGENCIES_2 = List.of("AGENCY2_ID");
    public static final List<String> PRODUCTS_1 = List.of("PRODUCT1_ID");
    public static final List<String> PRODUCTS_2 = List.of("PRODUCT2_ID");
    public static final ConfigSituation SITUATION_IN_FORCE = ConfigSituation.IN_FORCE;
    public static final ConfigSituation SITUATION_IN_ANALYSIS = ConfigSituation.IN_ANALYSIS;
    public static final ConfigSituation SITUATION_CLOSED = ConfigSituation.CLOSED;
    public static final List<ProposalChannel> PROPOSAL_CHANNELS = List.of(ProposalChannel.AGENCY);
    public static final String PRODUCT_TYPE_1 = "LCA";
    public static final String PRODUCT_TYPE_2 = "RDC";
    public static final Boolean TASK_IS_COMPLETED_1 = true;
    public static final Boolean TASK_IS_COMPLETED_2 = true;
    public static final String TASK_ID_1 = "id-1";
    public static final TaskType TASK_TYPE_VOTE_1 = TaskType.VOTE;
    public static final Boolean IS_CONNECTED_PERSON_1 = true;
    public static final Boolean IS_STATUTORY_MEMBER_1 = true;
    public static final Number MIN_RATE_PERCENT_1 = 100.1;
    public static final Number MAX_RATE_PERCENT_1 = 130;
    public static final List<User> TASK_USERS_1 = List.of(defaultUserCreate());
    public static final TaskType TASK_TYPE_VOTE_2 = TaskType.VOTE;
    public static final Boolean IS_CONNECTED_PERSON_2 = false;
    public static final Boolean IS_STATUTORY_MEMBER_2 = false;
    public static final Number MIN_RATE_PERCENT_2 = 110.1;
    public static final Number MAX_RATE_PERCENT_2 = 130;
    public static final List<User> TASK_USERS_2 = List.of(defaultUserUpdate());

    public static ConfigDTO defaultConfigRequest() {
        final ConfigDTO configDTO = new ConfigDTO();
        configDTO.setTasks(defaultTasksConfig());
        configDTO.setCooperativeCode(COOPERATIVE_CODE_1);
        configDTO.setProposalChannel(PROPOSAL_CHANNELS);
        configDTO.setRuleNumber(RULE_NUMBER_1);
        configDTO.setAgencies(AGENCIES_1);
        configDTO.setProductType(PRODUCT_TYPE_1);
        configDTO.setSituation(SITUATION_IN_ANALYSIS);
        configDTO.setProducts(PRODUCTS_1);
        return configDTO;
    }

    public static ConfigUpdateDTO defaultConfigUpdate() {
       final ConfigUpdateDTO configUpdateDTO = new ConfigUpdateDTO();
        configUpdateDTO.setCooperativeCode(COOPERATIVE_CODE_2);
        configUpdateDTO.setProposalChannel(PROPOSAL_CHANNELS);
        configUpdateDTO.setAgencies(AGENCIES_2);
        configUpdateDTO.setProductType(PRODUCT_TYPE_2);
        configUpdateDTO.setSituation(SITUATION_IN_FORCE);
        configUpdateDTO.setProducts(PRODUCTS_2);
       return configUpdateDTO;
    }

    public static ConfigUpdateDTO defaultConfigUpdateInvalid() {
        final ConfigUpdateDTO configUpdateDTO = new ConfigUpdateDTO();
        configUpdateDTO.setCooperativeCode(null);
        configUpdateDTO.setAgencies(List.of());
        configUpdateDTO.setProductType(null);
        configUpdateDTO.setSituation(null);
        configUpdateDTO.setProducts(List.of());
        return configUpdateDTO;
    }

    public static ConfigCreateDTO defaultConfigCreate() {
        final ConfigCreateDTO configCreateDTO = new ConfigCreateDTO();
        configCreateDTO.setCooperativeCode(COOPERATIVE_CODE_1);
        configCreateDTO.setProposalChannel(PROPOSAL_CHANNELS);
        configCreateDTO.setAgencies(AGENCIES_1);
        configCreateDTO.setProductType(PRODUCT_TYPE_1);
        configCreateDTO.setSituation(SITUATION_IN_ANALYSIS);
        configCreateDTO.setProducts(PRODUCTS_1);
        return configCreateDTO;
    }

    public static List<Task> defaultTasksConfig() {
        final Task task = new Task();
        final List<Task> tasks = new ArrayList<>();
        task.setId(TASK_ID_1);
        task.setType(TASK_TYPE_VOTE_1);
        task.setConnectedPerson(IS_CONNECTED_PERSON_1);
        task.setStatutoryMember(IS_STATUTORY_MEMBER_1);
        task.setMinRatePercent(MIN_RATE_PERCENT_1);
        task.setMaxRatePercent(MAX_RATE_PERCENT_1);
        task.setCompleted(TASK_IS_COMPLETED_1);
        task.setUsers(TASK_USERS_1);
        tasks.add(task);
        return tasks;
    }

    public static TaskCreateDTO defaultTaskCreate() {
        final TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setId(TASK_ID_1);
        taskCreateDTO.setType(TASK_TYPE_VOTE_1);
        taskCreateDTO.setConnectedPerson(IS_CONNECTED_PERSON_1);
        taskCreateDTO.setStatutoryMember(IS_STATUTORY_MEMBER_1);
        taskCreateDTO.setMinRatePercent(MIN_RATE_PERCENT_1);
        taskCreateDTO.setMaxRatePercent(MAX_RATE_PERCENT_1);
        taskCreateDTO.setCompleted(TASK_IS_COMPLETED_1);
        taskCreateDTO.setUsers(TASK_USERS_1);
        return taskCreateDTO;
    }

    public static TaskUpdateDTO defaultTaskUpdate() {
        final TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setId(TASK_ID_1);
        taskUpdateDTO.setType(TASK_TYPE_VOTE_2);
        taskUpdateDTO.setConnectedPerson(IS_CONNECTED_PERSON_2);
        taskUpdateDTO.setStatutoryMember(IS_STATUTORY_MEMBER_2);
        taskUpdateDTO.setMinRatePercent(MIN_RATE_PERCENT_2);
        taskUpdateDTO.setMaxRatePercent(MAX_RATE_PERCENT_2);
        taskUpdateDTO.setUsers(TASK_USERS_2);
        taskUpdateDTO.setCompleted(TASK_IS_COMPLETED_2);
        return taskUpdateDTO;
    }

    public static ProductParameterDTO defaultProductParameter() {
        ProductParameterDTO productParameterDTO = new ProductParameterDTO();
        productParameterDTO.setMaxRate(100);
        productParameterDTO.setMaxProductRate(130);
        return productParameterDTO;
    }

    public static User defaultUserCreate() {
        User user = new User();
        user.setRole("Gestora");
        user.setName("Paula de Souza");
        user.setLdap("paula_desouza");
        user.setAgency("0101 - AGENCY");
        return user;
    }

    public static User defaultUserUpdate() {
        User user = new User();
        user.setRole("Administrator");
        user.setName("Jhon");
        user.setLdap("jhon");
        user.setAgency("0116 - AGENCY");
        return user;
    }

}
