package com.example.sims.model;

import lombok.Data;

@Data
public class Response<T> {
    private int status; // HTTP status code
    private String message; // Response message
    private T data; // Response data (e.g., an item or list of items)

    public Response(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}