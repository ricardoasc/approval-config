package io.sicredi.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.builders.*;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.Collection;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfiguration {

    @Bean
    public Docket api(@Value("${spring.application.name}") String applicationName,
                      @Value("${SERVER_CONTEXT_PATH:/}") String contextPath,
                      @Value("${VERSION:latest}") final String version) {

        TypeResolver typeResolver = new TypeResolver();
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(version)
                .apiInfo(getApiInfo(version, applicationName))
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.sicredi"))
                .paths(PathSelectors.any())
                .build()
                .pathMapping(contextPath)
                .alternateTypeRules(
                        AlternateTypeRules.newRule(typeResolver.resolve(Flux.class, WildcardType.class), typeResolver.resolve(Collection.class, WildcardType.class)),
                        AlternateTypeRules.newRule(typeResolver.resolve(Flux.class, typeResolver.resolve(ResponseEntity.class, WildcardType.class)), typeResolver.resolve(Collection.class, WildcardType.class)),
                        AlternateTypeRules.newRule(typeResolver.resolve(Mono.class, typeResolver.resolve(ResponseEntity.class, WildcardType.class)), typeResolver.resolve(WildcardType.class)),
                        AlternateTypeRules.newRule(typeResolver.resolve(Mono.class, WildcardType.class), typeResolver.resolve(WildcardType.class))
                );
    }

    private ApiInfo getApiInfo(final String version, final String applicationName) {
        return new ApiInfoBuilder()
                .title(applicationName)
                .description("Investment Approval Config API")
                .version(version)
                .build();
    }
}