package com.github.youngteurus.servletdatabase.models.error;

public class ErrorMessage {
    private final int error;
    private final String message;

    public ErrorMessage(int error, String message) {
        this.error = error;
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "error=" + error +
                ", message='" + message + '\'' +
                '}';
    }
}
