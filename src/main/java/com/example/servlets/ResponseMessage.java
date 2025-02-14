package com.example.servlets;

public class ResponseMessage {
    private String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }
}
