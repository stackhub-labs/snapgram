package com.instagram.post.repository;

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

    // ✅ 해당 유저의 게시글 수
    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM post WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
        return count != null ? count : 0;
    }

    // ✅ 해당 유저의 게시글 요약 목록 (id, image_url만)
    public List<Map<String, Object>> findPostSummariesByUserId(Long userId) {
        String sql = "SELECT id, image_url FROM post WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> mapToPostSummary(rs));
    }

    private Map<String, Object> mapToPostSummary(ResultSet rs) throws SQLException {
        Map<String, Object> post = new HashMap<>();
        post.put("id", rs.getLong("id"));
        post.put("image_url", rs.getString("image_url"));
        return post;
    }
}
