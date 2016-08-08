package com.github.slamdev.ripe.integration;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;

import java.net.URI;

import static org.springframework.hateoas.Link.REL_SELF;

public final class HeaderUtils {

    private HeaderUtils() {
        // Utility class
    }

    public static HttpHeaders selfLocation(ResourceSupport resource) {
        HttpHeaders headers = new HttpHeaders();
        if (resource != null) {
            Link link = resource.getLink(REL_SELF);
            if (link != null) {
                headers.setLocation(URI.create(link.getHref()));
            }
        }
        return headers;
    }
}
