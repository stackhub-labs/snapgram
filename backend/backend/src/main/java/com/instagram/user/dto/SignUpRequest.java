package com.instagram.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.instagram.user.model.User;

public class SignUpRequest {
    private String name;
    private String email;
    private String password;
    private String nickname;
    @JsonProperty("is_agree")
    private boolean isAgree;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isAgree() {
        return isAgree;
    }

    public void setAgree(boolean agree) {
        isAgree = agree;
    }

    public User toEntity() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setNickname(this.nickname);
        return user;
    }
}
