package com.github.slamdev.catalog.load.balancer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.concurrent.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoadBalancerTest {

    private static final String HOST_1 = "http://fake-host1.com";

    private static final String HOST_2 = "http://fake-host2.com";

    private LoadBalancer balancer;

    private LoadBalancedRequest<String> request;

    @Rule
    public ExpectedException expectedException = none();

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        balancer = new LoadBalancer(asList(HOST_1, HOST_2));
        request = mock(LoadBalancedRequest.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_no_hosts_provided() {
        new LoadBalancer(emptyList());
    }

    @Test
    public void should_execute_provided_request() throws IOException {
        when(request.execute(anyString(), anyString())).thenReturn("some-response");
        String response = balancer.executeRequest("", "", request);
        assertThat(response, is("some-response"));
    }

    @Test
    public void should_append_host_to_request_uri() throws IOException {
        when(request.execute(eq(HOST_1 + "/uri"), anyString())).thenReturn("some-response");
        String response = balancer.executeRequest("/uri", "", request);
        assertThat(response, is("some-response"));
    }

    @Test
    public void should_use_next_host_when_request_throws_io() throws IOException {
        when(request.execute(eq(HOST_1 + "/uri"), anyString())).thenThrow(new IOException());
        when(request.execute(eq(HOST_2 + "/uri"), anyString())).thenReturn("some-response");
        String response = balancer.executeRequest("/uri", "", request);
        assertThat(response, is("some-response"));
    }

    @Test(expected = IOException.class)
    public void should_throw_io_when_request_to_all_hosts_throws_io() throws IOException {
        when(request.execute(eq(HOST_1 + "/uri"), anyString())).thenThrow(new IOException());
        when(request.execute(eq(HOST_2 + "/uri"), anyString())).thenThrow(new IOException());
        balancer.executeRequest("/uri", "", request);
    }

    @Test
    public void should_contain_last_cause_when_request_to_all_hosts_throws_io() throws IOException {
        IOException exception = new IOException("failed");
        expectedException.expectCause(is(exception));
        when(request.execute(eq(HOST_1 + "/uri"), anyString())).thenThrow(new IOException());
        when(request.execute(eq(HOST_2 + "/uri"), anyString())).thenThrow(exception);
        balancer.executeRequest("/uri", "", request);
    }

    @Test
    public void should_use_free_host_when_other_contains_operation() throws IOException, ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        LoadBalancedRequest<String> request1 = (uri, method) -> {
            safeAwait(latch);
            return uri.equals(HOST_1 + "/uri") ? "response1" : null;
        };
        LoadBalancedRequest<String> request2 = (uri, method) -> {
            latch.countDown();
            return uri.equals(HOST_2 + "/uri") ? "response2" : null;
        };
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<String> response1 = executor.submit(() -> balancer.executeRequest("/uri", "", request1));
        Future<String> response2 = executor.submit(() -> balancer.executeRequest("/uri", "", request2));
        assertThat(response1.get(), is("response1"));
        assertThat(response2.get(), is("response2"));
    }

    private void safeAwait(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }
    }
}
