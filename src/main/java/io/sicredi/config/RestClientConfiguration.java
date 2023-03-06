package io.sicredi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class RestClientConfiguration {
    public static final String WEBCLIENT_URL = "Creating WebClient in URL: {}";
    @Value("${sicredi.investment-product-catalog.url}")
    private String investmentProductCatalogUrl;

    @Bean
    @Lazy
    public WebClient productClient(final WebClient.Builder webClientBuilder) {
        log.debug(WEBCLIENT_URL, investmentProductCatalogUrl);
        return webClientBuilder
                .filter(logRequest())
                .baseUrl(investmentProductCatalogUrl)
                .build();
    }
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }
}
