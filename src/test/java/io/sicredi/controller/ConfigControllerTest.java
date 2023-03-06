package io.sicredi.controller;

import io.sicredi.client.ProductCatalogClient;
import io.sicredi.common.WebFluxIntegrationTest;
import io.sicredi.dto.ConfigCreateDTO;
import io.sicredi.dto.ConfigDTO;
import io.sicredi.service.ConfigService;
import io.sicredi.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import static io.sicredi.factory.ConfigTestFactory.*;

class ConfigControllerTest extends WebFluxIntegrationTest {
    @Autowired
    private ConfigService configService;
    @Autowired
    private TaskService taskService;
    @MockBean
    private ProductCatalogClient productCatalogClient;

    private ConfigDTO configDTO;

    @BeforeEach
    void setUp() {
        configDTO = configService.save(defaultConfigCreate(),
                "internal-username", "internal-name").block();
        Mockito.when(productCatalogClient.getParametersProduct(
                        configDTO.getProducts().get(0),
                        configDTO.getCooperativeCode()))
                .thenReturn(Mono.just(defaultProductParameter()));
    }

    @Test
    @DisplayName("Test get the settings search by filters")
    public void testGetSettingsSearchByFilters() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/config")
                        .queryParam("cooperativeCode", COOPERATIVE_CODE_1)
                        .queryParam("situation", String.valueOf(SITUATION_IN_FORCE))
                        .queryParam("ruleNumber", RULE_NUMBER_1)
                        .queryParam("productType", PRODUCT_TYPE_1)
                        .queryParam("products", PRODUCTS_1)
                        .queryParam("agencies", AGENCIES_1)
                        .build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody();
    }

    @Test
    @DisplayName("Test to insert a configuration")
    public void testInsertSetting() {
        webTestClient
                .post()
                .uri("/config")
                .bodyValue(defaultConfigRequest())
                .header("username", "LDAP")
                .header("name", "Name from user")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.cooperativeCode").isEqualTo(COOPERATIVE_CODE_1)
                .jsonPath("$.agencies[0]").isEqualTo(AGENCIES_1.get(0))
                .jsonPath("$.products[0]").isEqualTo(PRODUCTS_1.get(0))
                .jsonPath("$.situation").isEqualTo(String.valueOf(SITUATION_IN_ANALYSIS))
                .jsonPath("$.productType").isEqualTo(PRODUCT_TYPE_1);
    }

    @Test
    @DisplayName("Test update a configuration")
    public void testUpdateSetting() {
        Mockito.when(productCatalogClient.getParametersProduct(
                        defaultConfigUpdate().getProducts().get(0),
                        defaultConfigUpdate().getCooperativeCode()))
                .thenReturn(Mono.just(defaultProductParameter()));

        taskService.createNewTask(
                configDTO.getId(), defaultTaskCreate(),
                "internal-username",
                "internal-user")
                .block();

        webTestClient
                .put()
                .uri("/config/" + configDTO.getId())
                .bodyValue(defaultConfigUpdate())
                .header("username", "LDAP")
                .header("name", "Name from user")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.tasks[0]").isNotEmpty()
                .jsonPath("$.agencies[0]").isEqualTo(AGENCIES_2.get(0))
                .jsonPath("$.products[0]").isEqualTo(PRODUCTS_2.get(0))
                .jsonPath("$.cooperativeCode").isEqualTo(COOPERATIVE_CODE_2)
                .jsonPath("$.situation").isEqualTo(String.valueOf(SITUATION_IN_FORCE))
                .jsonPath("$.productType").isEqualTo(PRODUCT_TYPE_2);
    }

    @Test
    @DisplayName("Test get configuration by ID")
    public void testGetSettingById() {
        webTestClient
                .get()
                .uri("/config/" + configDTO.getId())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").exists();
    }

    @Test
    @DisplayName("Test configuration inactivation")
    public void testInactiveSetting() {
        webTestClient
                .delete()
                .uri("/config/" + configDTO.getId())
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        webTestClient
                .get()
                .uri("/config/" + configDTO.getId())
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @DisplayName("Test bad request in save configuration")
    public void testBadRequestInSaveConfiguration() {

        final ConfigCreateDTO configCreateDTO = defaultConfigCreate();
        configCreateDTO.setProducts(null);
        configCreateDTO.setCooperativeCode(null);

        webTestClient
                .post()
                .uri("/config")
                .header("username", "LDAP")
                .header("name", "Name from user")
                .bodyValue(configCreateDTO)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @DisplayName("Test bad request in update configuration")
    public void testBadRequestInUpdateConfiguration() {
        webTestClient
                .put()
                .uri("/config/", configDTO.getId())
                .header("username", "LDAP")
                .header("name", "Name from user")
                .bodyValue(defaultConfigUpdateInvalid())
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @DisplayName("Test not found get configuration by ID")
    public void testNotFoundGetConfigurationById() {
        webTestClient
                .get()
                .uri("/config/" + CONFIG_ID_1)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @DisplayName("Test not found on inactive configuration")
    public void testNotFoundOnInactiveConfigutation() {
        webTestClient
                .delete()
                .uri("/config/" + CONFIG_ID_1)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @DisplayName("Test insert new task in configuration")
    public void testInsertNewTaskInConfiguration() {
        webTestClient
                .post()
                .uri("/config/" + configDTO.getId() + "/tasks")
                .header("username", "LDAP")
                .header("name", "Name from user")
                .bodyValue(defaultTaskCreate())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.type").isEqualTo(String.valueOf(TASK_TYPE_VOTE_1))
                .jsonPath("$.connectedPerson").isEqualTo(IS_CONNECTED_PERSON_1)
                .jsonPath("$.statutoryMember").isEqualTo(IS_STATUTORY_MEMBER_1)
                .jsonPath("$.completed").isEqualTo(TASK_IS_COMPLETED_1)
                .jsonPath("$.minRatePercent").isEqualTo(MIN_RATE_PERCENT_1)
                .jsonPath("$.maxRatePercent").isEqualTo(MAX_RATE_PERCENT_1)
                .jsonPath("$.users[0].ldap").isEqualTo(defaultUserCreate().getLdap())
                .jsonPath("$.users[0].name").isEqualTo(defaultUserCreate().getName())
                .jsonPath("$.users[0].role").isEqualTo(defaultUserCreate().getRole())
                .jsonPath("$.users[0].agency").isEqualTo(defaultUserCreate().getAgency());
    }

    @Test
    @DisplayName("Test update task from configuration")
    public void testUpdateTaskFromConfiguration() {
        var task= taskService.createNewTask(
                        configDTO.getId(), defaultTaskCreate(),
                        "internal-username",
                        "internal-user")
                .block();

        webTestClient
                .put()
                .uri("/config/" + configDTO.getId() + "/tasks/" +
                        task.getId())
                .header("username", "LDAP")
                .header("name", "Name from user")
                .bodyValue(defaultTaskUpdate())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.connectedPerson").isEqualTo(IS_CONNECTED_PERSON_2)
                .jsonPath("$.statutoryMember").isEqualTo(IS_STATUTORY_MEMBER_2)
                .jsonPath("$.completed").isEqualTo(TASK_IS_COMPLETED_2)
                .jsonPath("$.minRatePercent").isEqualTo(MIN_RATE_PERCENT_2)
                .jsonPath("$.maxRatePercent").isEqualTo(MAX_RATE_PERCENT_2)
                .jsonPath("$.users[0].ldap").isEqualTo(defaultUserUpdate().getLdap())
                .jsonPath("$.users[0].name").isEqualTo(defaultUserUpdate().getName())
                .jsonPath("$.users[0].role").isEqualTo(defaultUserUpdate().getRole())
                .jsonPath("$.users[0].agency").isEqualTo(defaultUserUpdate().getAgency());
    }

    @Test
    @DisplayName("Test delete task from configuration")
    public void testDeleteTaskFromConfiguration() {
        var task = taskService.createNewTask(
                        configDTO.getId(), defaultTaskCreate(),
                        "internal-username",
                        "internal-user")
                .block();

        webTestClient
                .delete()
                .uri("/config/" + configDTO.getId() + "/tasks/" +
                        task.getId())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(ConfigDTO.class);

        webTestClient
                .get()
                .uri("/config/" + configDTO.getId() + "/tasks/" +
                        task.getId())
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @DisplayName("Test delete task when does not exist")
    public void testDeleteTaskWhenDoesNotExist() {
        webTestClient
                .delete()
                .uri("/config/" + configDTO.getId() + "/tasks/" + TASK_ID_1)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    @DisplayName("Test update task when does not exist")
    public void testUpdateTaskWhenDoesNotExist() {
        webTestClient
                .put()
                .uri("/config/" + configDTO.getId() + "/tasks/" + TASK_ID_1)
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }
}