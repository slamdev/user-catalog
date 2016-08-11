package com.github.slamdev.catalog.load.balancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;

public class LoadBalancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancer.class);

    private final Queue<Server> liveServers = new PriorityBlockingQueue<>();

    private Set<Server> failedServers = new CopyOnWriteArraySet<>();

    public LoadBalancer(List<String> hosts) {
        if (hosts == null || hosts.isEmpty()) {
            throw new IllegalArgumentException("At least one host should be provided");
        }
        hosts.stream().map(Server::new).forEach(liveServers::add);
    }

    public <T> T executeRequest(String uri, String method, LoadBalancedRequest<T> request) throws IOException {
        Server server = liveServers.poll();
        if (server == null) {
            throw new IOException("All servers are not available.");
        }
        Operation operation = new Operation(uri, method);
        server.getOperations().add(operation);
        liveServers.offer(server);
        try {
            return request.execute(server.getHost() + uri, method);
        } catch (IOException e) {
            LOGGER.info("", e);
            liveServers.remove(server);
            failedServers.add(server);
            return executeRequest(uri, method, request);
        }
    }
}
