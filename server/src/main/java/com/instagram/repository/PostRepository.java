package com.instagram.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM post WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public List<Map<String, Object>> findPostSummariesByUserId(Long userId) {
        String sql = "SELECT id, image_url FROM post WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapToPostSummary(rs), userId);
    }

    private Map<String, Object> mapToPostSummary(ResultSet rs) throws SQLException {
        Map<String, Object> post = new HashMap<>();
        post.put("id", rs.getLong("id"));
        post.put("image_url", rs.getString("image_url"));
        return post;
    }
}
