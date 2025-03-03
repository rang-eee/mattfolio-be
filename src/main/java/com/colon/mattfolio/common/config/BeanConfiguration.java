package com.colon.mattfolio.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient() {
        return WebClient.builder()
            .build();
    }

}
