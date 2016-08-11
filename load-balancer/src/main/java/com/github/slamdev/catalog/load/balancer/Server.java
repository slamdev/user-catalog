package com.github.slamdev.catalog.load.balancer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
class Server implements Comparable<Server> {
    private final String host;
    private final List<Operation> operations = new ArrayList<>();

    @Override
    public int compareTo(Server server) {
        return Integer.compare(operations.size(), server.operations.size());
    }
}
