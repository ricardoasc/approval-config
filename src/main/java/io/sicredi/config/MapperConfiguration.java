package io.sicredi.config;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    public MapperFactory getMapperFactory() {
        return new DefaultMapperFactory.Builder()
                .build();
    }
}
