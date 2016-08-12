package com.github.slamdev.catalog.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.springframework.boot.SpringApplication.exit;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        restTemplate.getForEntity("/index.html", String.class);
        System.out.println(Arrays.toString(args));
        exit(context);
    }
}
