package com.hw.db.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    private String message;
    private Object data;

    @JsonCreator
    public Message(
            @JsonProperty("message") Enum<?> message,
            @JsonProperty("data") Object data
    ) {
        this.message = message.toString();
        this.data = data;
    }

    @JsonCreator
    public Message(@JsonProperty("message") Enum<?> message) {
        this.message = message.toString();
        this.data = null;
    }

    @JsonCreator
    public Message(@JsonProperty("data") Object data) {
        this.message = null;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}



