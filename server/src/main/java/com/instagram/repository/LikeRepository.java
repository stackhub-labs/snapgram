package com.instagram.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public LikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countLikesByPostId(Long postId) {
        String sql = "SELECT COUNT(*) FROM `like` WHERE post_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
        return count != null ? count : 0;
    }

    public boolean isLikedByUser(Long userId, Long postId) {
        String sql = "SELECT COUNT(*) FROM `like` WHERE user_id = ? AND post_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, postId);
        return count != null && count > 0;
    }

    public void like(Long userId, Long postId) {
        String sql = "INSERT INTO `like` (user_id, post_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, postId);
    }

    public void unlike(Long userId, Long postId) {
        String sql = "DELETE FROM `like` WHERE user_id = ? AND post_id = ?";
        jdbcTemplate.update(sql, userId, postId);
    }
} 