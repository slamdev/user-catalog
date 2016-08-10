package com.github.slamdev.catalog.loader;

import com.github.slamdev.catalog.loader.LoadBalancer.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

import static java.net.URI.create;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Component
public class LoadBalancedRequestFactory extends SimpleClientHttpRequestFactory {

    @Autowired
    private LoadBalancer loadBalancer;

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        Server server = loadBalancer.getFreeServer(uri, httpMethod);
        if (server == null) {
            throw new IllegalStateException("No servers available");
        }
        try {
            return super.createRequest(combine(server.getHost(), uri), httpMethod);
        } catch (IOException e) {
            loadBalancer.markServerFailed(server);
            return createRequest(uri, httpMethod);
        }
    }

    private URI combine(URI... uris) {
        return create(stream(uris).map(URI::toASCIIString).collect(joining("/")));
    }
}
