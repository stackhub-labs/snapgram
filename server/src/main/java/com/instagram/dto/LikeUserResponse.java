package com.instagram.dto;

public class LikeUserResponse {
    private Long user_id;
    private String username;

    public LikeUserResponse() {}

    public LikeUserResponse(Long user_id, String username) {
        this.user_id = user_id;
        this.username = username;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
