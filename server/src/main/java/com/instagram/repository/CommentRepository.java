package com.instagram.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long userId, Long postId, String content) {
        String sql = "INSERT INTO comment (user_id, post_id, content) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, postId, content);
    }

    public boolean existsByPostId(Long postId) {
        String sql = "SELECT COUNT(*) FROM post WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
        return count != null && count > 0;
    }

    public int countCommentsByPostId(Long postId) {
        String sql = "SELECT COUNT(*) FROM comment WHERE post_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
        return count != null ? count : 0;
    }

    public boolean existsById(Long commentId) {
        String sql = "SELECT COUNT(*) FROM comment WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, commentId);
        return count != null && count > 0;
    }

    public Long getCommentUserId(Long commentId) {
        String sql = "SELECT user_id FROM comment WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, commentId);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteById(Long commentId) {
        String sql = "DELETE FROM comment WHERE id = ?";
        jdbcTemplate.update(sql, commentId);
    }
} 