package com.instagram.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

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

    public List<Map<String, Object>> getLikesByPostId(Long postId) {
        String sql = "SELECT l.user_id, u.nickname as username " +
                    "FROM `like` l " +
                    "JOIN user u ON l.user_id = u.id " +
                    "WHERE l.post_id = ? " +
                    "ORDER BY l.user_id";
        return jdbcTemplate.queryForList(sql, postId);
    }

    public void deleteLikesByPostId(Long postId) {
        String sql = "DELETE FROM `like` WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }
} 