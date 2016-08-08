package com.github.slamdev.ripe.business.isp.boundary;

public class NotFoundException extends IllegalArgumentException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String s) {
        super(s);
    }
}
