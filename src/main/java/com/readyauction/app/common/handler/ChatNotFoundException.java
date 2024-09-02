package com.readyauction.app.common.handler;

public class ChatNotFoundException extends RuntimeException{
    public ChatNotFoundException(String message){
        super(message);
    }
}
