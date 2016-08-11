package com.github.slamdev.catalog.load.balancer;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class LoadBalancer {

    private final Queue<Server> servers = new PriorityBlockingQueue<>();

    public LoadBalancer(List<String> hosts) {
        if (hosts == null || hosts.isEmpty()) {
            throw new IllegalArgumentException("At least one host should be provided");
        }
        hosts.stream().map(Server::new).forEach(servers::add);
    }

    public <T> T executeRequest(String uri, String method, LoadBalancedRequest<T> request) throws IOException {
        Server server = servers.poll();
        Operation operation = new Operation(uri, method);
        server.getOperations().add(operation);
        servers.offer(server);
        return request.execute(server.getHost() + uri, method);
    }
}
