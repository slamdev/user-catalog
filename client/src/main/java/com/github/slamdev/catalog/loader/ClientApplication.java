package com.github.slamdev.catalog.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import static java.lang.Integer.parseInt;
import static org.springframework.boot.SpringApplication.exit;

@SpringBootApplication
@PropertySource(value = "file:${user.home}/.gradle/gradle.properties", ignoreResourceNotFound = true)
public class ClientApplication implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private OperationsExecutor executor;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (args.length < 1) {
            throw new CommandLineUsageException();
        }
        executor.execute(parseInt(args[0]));
        exit(context);
    }
}
