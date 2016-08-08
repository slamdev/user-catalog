package com.github.slamdev.ripe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class BaseUrlProvider {

    @Autowired
    private Environment environment;

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public String produceBaseUrl() {
        int port = environment.getProperty("local.server.port", Integer.class, 0);
        return "http://localhost:" + port;
    }
}
