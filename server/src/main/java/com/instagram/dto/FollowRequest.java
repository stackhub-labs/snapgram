package com.instagram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FollowRequest {
    @JsonProperty("user_id")
    private Long userId;

    public FollowRequest() {
    }

    public FollowRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}