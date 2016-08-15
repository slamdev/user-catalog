package com.github.slamdev.catalog.loader;

import org.springframework.boot.ExitCodeGenerator;

public class CommandLineUsageException extends RuntimeException implements ExitCodeGenerator {

    private static final String USAGE_MESSAGE = "You should pass number of operations, eg. \"java -jar client.jar 500\"";

    public CommandLineUsageException() {
        super(USAGE_MESSAGE);
    }

    @Override
    public int getExitCode() {
        return 1;
    }
}
