package com.github.slamdev.catalog.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class LoadBalancedRestTemplateConfiguration {

    @Autowired
    private RestTemplateBuilder builder;

    @Autowired
    private LoadBalancedRequestFactory factory;

    @Bean
    public RestTemplate restTemplate() {
        return builder.requestFactory(factory).build();
    }
}
