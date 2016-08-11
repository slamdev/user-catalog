package com.github.slamdev.catalog.load.balancer;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import static java.time.Duration.ZERO;
import static java.util.Comparator.comparing;

@Data
@RequiredArgsConstructor
class Server implements Comparable<Server> {
    private final String host;
    private Duration duration = ZERO;

    @Override
    public int compareTo(Server server) {
        return comparing(Server::getDuration).thenComparing(Server::getHost).compare(this, server);
    }
}
