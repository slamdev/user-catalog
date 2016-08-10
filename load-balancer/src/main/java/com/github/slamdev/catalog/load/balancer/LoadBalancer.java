package com.github.slamdev.catalog.load.balancer;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class LoadBalancer {

    private final List<Server> servers;

    public LoadBalancer(List<String> hosts) {
        if (hosts == null || hosts.isEmpty()) {
            throw new IllegalArgumentException("At least one host should be provided");
        }
        servers = hosts.stream().map(Server::new).collect(toList());
    }

    public <T> T executeRequest(String uri, String method, LoadBalancedRequest<T> request) throws IOException {
        IOException lastException = null;
        for (Server server : servers) {
            try {
                Operation operation = new Operation(uri, method);
                server.getOperations().add(operation);
                T response = request.execute(server.getHost() + uri, method);
                server.getOperations().remove(operation);
                return response;
            } catch (IOException e) {
                lastException = e;
            }
        }
        throw new IOException("Request execution failed on all server", lastException);
    }
}
