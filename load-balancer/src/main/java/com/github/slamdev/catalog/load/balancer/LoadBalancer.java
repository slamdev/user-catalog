package com.github.slamdev.catalog.load.balancer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

import static java.time.Duration.ZERO;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class LoadBalancer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancer.class);

    private final ScheduledExecutorService failedServersChecker = Executors.newScheduledThreadPool(1);

    private final Queue<Server> liveServers;

    private final Set<Server> failedServers = new CopyOnWriteArraySet<>();

    private final Map<Operation, Duration> operationTime = new ConcurrentHashMap<>();

    private final HostAvailabilityChecker hostAvailabilityChecker;

    public LoadBalancer(List<String> hosts, HostAvailabilityChecker hostAvailabilityChecker, Duration hostAvailabilityCheckDuration) {
        if (hosts == null || hosts.isEmpty()) {
            throw new IllegalArgumentException("At least one host should be provided");
        }
        liveServers = hosts.stream().map(Server::new).collect(toCollection(PriorityBlockingQueue::new));
        this.hostAvailabilityChecker = hostAvailabilityChecker;
        failedServersChecker.scheduleAtFixedRate(
                this::checkFailedServers,
                hostAvailabilityCheckDuration.toNanos(),
                hostAvailabilityCheckDuration.toNanos(),
                NANOSECONDS
        );
    }

    private void checkFailedServers() {
        List<Server> revivedServers = failedServers.stream()
                .filter(server -> hostAvailabilityChecker.isHostAvailable(server.getHost()))
                .collect(toList());
        if (!revivedServers.isEmpty()) {
            LOGGER.debug("Found revived servers: {}", revivedServers);
        }
        failedServers.removeAll(revivedServers);
        liveServers.addAll(revivedServers);
    }

    public <T> T executeRequest(String uri, String method, LoadBalancedRequest<T> request) throws IOException {
        Server server = liveServers.poll();
        LOGGER.debug("For [{}] uri and [{}] method trying to use [{}] server\nOperations duration cache is: {}",
                uri, method, server, operationTime);
        if (server == null) {
            throw new IOException("All servers are not available.");
        }
        Operation operation = new Operation(uri, method);
        Duration duration = operationTime.computeIfAbsent(operation, k -> ZERO);
        server.setDuration(server.getDuration().plus(duration));
        liveServers.offer(server);
        try {
            Instant clock = now();
            T response = request.execute(server.getHost() + uri, method);
            operationTime.put(operation, between(clock, now()));
            return response;
        } catch (IOException e) {
            liveServers.remove(server);
            server.setDuration(ZERO);
            failedServers.add(server);
            LOGGER.debug("Exception occurred for [" +
                    server + "] server. Adding server to failed list\nFailed servers: " +
                    failedServers, e);
            return executeRequest(uri, method, request);
        }
    }
}
