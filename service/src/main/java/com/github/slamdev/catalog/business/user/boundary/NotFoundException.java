package com.github.slamdev.catalog.business.user.boundary;

public class NotFoundException extends IllegalArgumentException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String s) {
        super(s);
    }
}
