package com.hoomicorp.hoomi.model.dto;

import com.google.gson.GsonBuilder;

import java.util.Objects;

public class LiveChatMessageDto {
    private String message;
    private String username;

    public LiveChatMessageDto() {
    }

    public LiveChatMessageDto(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, LiveChatMessageDto.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveChatMessageDto that = (LiveChatMessageDto) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, username);
    }
}
