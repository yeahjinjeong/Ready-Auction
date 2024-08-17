package com.readyauction.app.common.handler;

public class CashNotFoundException extends RuntimeException {
    public CashNotFoundException(String message) {
        super(message);
    }
}
