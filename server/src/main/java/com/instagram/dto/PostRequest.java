package com.instagram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostRequest {
    private String content;
    
    @JsonProperty("image_url")
    private String imageUrl;

    public PostRequest() {
    }

    public PostRequest(String content, String imageUrl) {
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}