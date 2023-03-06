package io.sicredi.client;

import io.sicredi.client.dto.ProductParameterDTO;
import io.sicredi.services.platform.error.PlatformException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static io.sicredi.error.ErrorDefinitionImpl.IAC_PRODUCT_PARAMETERS_NOT_FOUND_MSG;
import static io.sicredi.error.Exceptions.notFoundException;
import static reactor.core.publisher.Mono.error;

@Service
@Slf4j
public class ProductCatalogClient {
    private final WebClient webClient;

    public ProductCatalogClient(@Qualifier(value = "productClient") WebClient webClient) {
        this.webClient = webClient;
    }
    public Mono<ProductParameterDTO> getParametersProduct(String product, String organization) {
        return webClient.get()
                .uri("/products/{product}/parameters/{organization}", product, organization)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        c -> error(notFoundException(IAC_PRODUCT_PARAMETERS_NOT_FOUND_MSG)))
                .onStatus(HttpStatus::is5xxServerError,
                        c -> error(new PlatformException("productCatalog: Service unavailable")))
                .bodyToMono(ProductParameterDTO.class)
                .doOnSubscribe(s -> log.info("Buscando parâmetros do produto: {}", s))
                .doOnError(e -> log.error("Erro ao buscar parâmetros do produto: {}", e))
                .doOnSuccess(s -> log.info("Busca de parâmetros do produto efetuada com sucesso", s));
    }
}
