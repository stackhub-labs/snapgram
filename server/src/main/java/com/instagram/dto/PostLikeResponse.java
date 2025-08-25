package com.instagram.dto;

import java.util.List;

public class PostLikeResponse {
    private Long post_id;
    private List<LikeUserResponse> likes;
    private int like_count;

    public PostLikeResponse() {}

    public PostLikeResponse(Long post_id, List<LikeUserResponse> likes, int like_count) {
        this.post_id = post_id;
        this.likes = likes;
        this.like_count = like_count;
    }

    public Long getPost_id() {
        return post_id;
    }

    public void setPost_id(Long post_id) {
        this.post_id = post_id;
    }

    public List<LikeUserResponse> getLikes() {
        return likes;
    }

    public void setLikes(List<LikeUserResponse> likes) {
        this.likes = likes;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }
}
