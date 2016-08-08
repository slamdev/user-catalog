package com.github.slamdev.catalog.loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class LoaderApplication implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println(Arrays.toString(args));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LoaderApplication.class, args);
    }
}
