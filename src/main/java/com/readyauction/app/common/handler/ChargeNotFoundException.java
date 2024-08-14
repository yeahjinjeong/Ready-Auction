package com.readyauction.app.common.handler;

public class ChargeNotFoundException extends RuntimeException {
    public ChargeNotFoundException(String message) {
        super(message);
    }
}
