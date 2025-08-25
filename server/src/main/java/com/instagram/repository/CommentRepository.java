package com.instagram.repository;

import com.instagram.model.Comment;
import java.util.List;
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

    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT c.id, c.user_id, c.post_id, c.content, c.created_at, " +
                    "u.id as user_id, u.nickname as username " +
                    "FROM comment c " +
                    "JOIN user u ON c.user_id = u.id " +
                    "WHERE c.post_id = ? " +
                    "ORDER BY c.created_at ASC";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setUserId(rs.getLong("user_id"));
            comment.setPostId(rs.getLong("post_id"));
            comment.setContent(rs.getString("content"));
            comment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return comment;
        }, postId);
    }

    public void deleteCommentsByPostId(Long postId) {
        String sql = "DELETE FROM comment WHERE post_id = ?";
        jdbcTemplate.update(sql, postId);
    }
} 