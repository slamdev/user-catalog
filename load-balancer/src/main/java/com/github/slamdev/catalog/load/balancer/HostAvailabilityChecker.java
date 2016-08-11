package com.github.slamdev.catalog.load.balancer;

public interface HostAvailabilityChecker {

    boolean isHostAvailable(String host);
}
