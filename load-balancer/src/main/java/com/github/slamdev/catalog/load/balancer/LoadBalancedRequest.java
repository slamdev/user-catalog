package com.github.slamdev.catalog.load.balancer;

import java.io.IOException;

public interface LoadBalancedRequest<T> {

    T execute(String uri, String method) throws IOException;
}
