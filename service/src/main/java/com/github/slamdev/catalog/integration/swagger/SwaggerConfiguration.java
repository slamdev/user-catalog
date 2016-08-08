package com.github.slamdev.catalog.integration.swagger;

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;

import static springfox.documentation.builders.PathSelectors.regex;

@Component
public class SwaggerConfiguration {

    @Value("${swagger.title}")
    private String title;

    @Value("${swagger.desc}")
    private String desc;

    @Value("${swagger.version}")
    private String version;

    @Value("${swagger.termsUrl}")
    private String termsUrl;

    @Value("${swagger.contact}")
    private String contact;

    @Value("${swagger.license}")
    private String license;

    @Value("${swagger.licenseUrl}")
    private String licenseUrl;

    ApiInfo apiInfo() {
        return new ApiInfo(title, desc, version, termsUrl, new Contact(contact, "", ""), license, licenseUrl);
    }

    @SuppressWarnings("Guava" /* springfox uses guava, unfortunately */)
    Predicate<String> paths() {
        return regex("/api.*");
    }
}
