package com.github.slamdev.catalog.loader;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.github.slamdev.load.balancer.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

@Configuration
public class HostsAutoUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostsAutoUpdater.class);

    @Autowired
    private AmazonEC2Client client;

    @Autowired
    private LoadBalancer loadBalancer;

    private ScheduledExecutorService newServersChecker;

    @Value("${hosts.auto.update.duration}")
    private long hostsAutoUpdateDuration;

    @Value("${ec2.instance.tag}")
    private String ec2InstanceTag;

    private List<String> lastAddedHosts;

    @PostConstruct
    public void init() {
        lastAddedHosts = new ArrayList<>();
        newServersChecker = Executors.newScheduledThreadPool(1);
        updateHosts();
        newServersChecker.scheduleAtFixedRate(
                this::updateHosts,
                hostsAutoUpdateDuration,
                hostsAutoUpdateDuration,
                SECONDS
        );
    }

    @PreDestroy
    public void dispose() {
        newServersChecker.shutdownNow();
    }

    private void updateHosts() {
        List<String> availableHosts = availableHosts();
        if (!lastAddedHosts.containsAll(availableHosts)) {
            availableHosts.removeAll(lastAddedHosts);
            lastAddedHosts.addAll(availableHosts);
            LOGGER.info("New hosts found, adding them to load balancer: {}", availableHosts);
//            loadBalancer.addHosts(availableHosts);
        }
    }

    private List<String> availableHosts() {
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.withFilters(new Filter("tag:tag", singletonList(ec2InstanceTag)));
        DescribeInstancesResult result = client.describeInstances(request);
        return result.getReservations().stream()
                .flatMap(r -> r.getInstances().stream())
                .filter(i -> !"'terminated'".equals(i.getState().getName()))
                .map(Instance::getPublicDnsName)
                .collect(toList());
    }
}
