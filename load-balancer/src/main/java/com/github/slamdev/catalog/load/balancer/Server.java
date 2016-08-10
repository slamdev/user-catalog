package com.github.slamdev.catalog.load.balancer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
class Server {
    private final String host;
    private final List<Operation> operations = new ArrayList<>();
}
