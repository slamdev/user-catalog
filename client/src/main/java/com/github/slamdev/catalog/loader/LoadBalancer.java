package com.github.slamdev.catalog.loader;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

import static java.time.Duration.ZERO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

@Component
public class LoadBalancer {

    private ScheduledExecutorService failedServersChecked = Executors.newScheduledThreadPool(1);

    private Set<Server> failedServers = new CopyOnWriteArraySet<>();

    private Map<Operation, Duration> operationTime = new ConcurrentHashMap<>();

    private ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    private Queue<Server> servers = new PriorityBlockingQueue<>();

    {
        servers.offer(new Server(URI.create("http://loc1alhost:8080"), ZERO));
    }

    @PostConstruct
    public void init() {
        failedServersChecked.scheduleAtFixedRate(this::verifyFailedServers, 10, 10, SECONDS);
    }

    private void verifyFailedServers() {
        List<Server> revivedServers = failedServers.stream().filter(this::isServerAvailable).collect(toList());
        failedServers.removeAll(revivedServers);
        servers.addAll(revivedServers);
    }

    private boolean isServerAvailable(Server server) {
        try {
            ClientHttpRequest request = requestFactory.createRequest(server.getHost(), GET);
            return OK.equals(request.execute().getStatusCode());
        } catch (IOException e) {
            return false;
        }
    }

    public Server getFreeServer(URI uri, HttpMethod httpMethod) {
        Duration duration = operationTime.computeIfAbsent(new Operation(uri, httpMethod), k -> ZERO);
        Server server = servers.poll();
        server.setCurrentTasksDuration(server.getCurrentTasksDuration().plus(duration));
        servers.offer(server);
        return server;
    }

    public void markServerFailed(Server failedServer) {
        servers.removeIf(server -> server.equals(failedServer));
        failedServer.setCurrentTasksDuration(ZERO);
        failedServers.add(failedServer);
    }

    @Data
    @AllArgsConstructor
    private static class Operation {
        private URI uri;
        private HttpMethod httpMethod;
    }

    @Data
    @AllArgsConstructor
    public static class Server implements Comparable<Server> {
        private URI host;
        private Duration currentTasksDuration;

        @Override
        public int compareTo(Server server) {
            return currentTasksDuration.compareTo(server.currentTasksDuration);
        }
    }
}
