package com.instagram.dto;

public class PostSummaryResponse {
    private Long id;
    private String content;
    private String image_url;
    private String created_at;
    private int like_count;
    private int comment_count;

    public PostSummaryResponse() {}

    public PostSummaryResponse(Long id, String content, String image_url, String created_at, int like_count, int comment_count) {
        this.id = id;
        this.content = content;
        this.image_url = image_url;
        this.created_at = created_at;
        this.like_count = like_count;
        this.comment_count = comment_count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }
}
