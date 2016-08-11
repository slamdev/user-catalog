package com.github.slamdev.catalog.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@SpringBootApplication
public class LoaderApplication implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    public static void main(String[] args) {
        SpringApplication.run(LoaderApplication.class, args);
    }

    @Override
    public void run(String... args) {
        restTemplate.getForEntity("http://google.com/index.htm", String.class);
        System.out.println(Arrays.toString(args));
    }
}
