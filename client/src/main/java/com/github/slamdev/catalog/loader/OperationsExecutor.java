package com.github.slamdev.catalog.loader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class OperationsExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationsExecutor.class);

    private static final HttpHeaders JSON_HEADERS = new HttpHeaders();

    static {
        JSON_HEADERS.setContentType(APPLICATION_JSON);
    }

    @Autowired
    private RestTemplate restTemplate;

    private AtomicInteger counter = new AtomicInteger();

    public void execute(int operationsCount) {
        counter.set(operationsCount);
        LOGGER.info("Execution started. Counter {}", counter.get());
        execute();
        LOGGER.info("Execution ended. Counter {}", counter.get());
    }

    private void execute() {
        LOGGER.info("Execution part started. Counter {}", counter.get());
        counter.decrementAndGet();
        JSONObject response = new JSONObject(restTemplate.getForObject("/api/user", String.class));
        toUsers(response).stream().parallel().map(this::getUser).forEach(this::deleteUser);
        if (counter.get() > 0) {
            range(0, magicSplit(counter.get())).parallel().forEach(this::createUser);
            LOGGER.info("Execution part ended. Counter {}", counter.get());
            execute();
        }
    }

    private int magicSplit(int itemsLeft) {
        return itemsLeft * 30 / 100;
    }

    private List<JSONObject> toUsers(JSONObject response) {
        if (response.opt("_embedded") != null) {
            JSONArray users = response.getJSONObject("_embedded").getJSONArray("users");
            return range(0, users.length()).mapToObj(users::getJSONObject).collect(toList());
        }
        return emptyList();
    }

    private void createUser(int index) {
        counter.decrementAndGet();
        JSONObject user = new JSONObject();
        user.put("userName", "user-" + index);
        user.put("firstName", randomUUID());
        user.put("lastName", randomUUID());
        HttpEntity<String> entity = new HttpEntity<>(user.toString(), JSON_HEADERS);
        restTemplate.postForLocation("/api/user", entity, Void.class);
    }

    private void deleteUser(JSONObject user) {
        counter.decrementAndGet();
        restTemplate.delete("/api/user/" + user.get("id"));
    }

    private JSONObject getUser(JSONObject user) {
        counter.decrementAndGet();
        return new JSONObject(restTemplate.getForObject("/api/user/" + user.get("id"), String.class));
    }
}
