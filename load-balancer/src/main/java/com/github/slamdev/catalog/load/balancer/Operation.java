package com.github.slamdev.catalog.load.balancer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Operation {
    private String uri;
    private String method;
}
