package com.github.slamdev.catalog.integration.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class SwaggerDocketProvider {

    @Autowired
    private SwaggerConfiguration configuration;

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(configuration.paths())
                .build()
                .apiInfo(configuration.apiInfo());
    }
}
