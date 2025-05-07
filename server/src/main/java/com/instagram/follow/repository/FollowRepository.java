package com.instagram.follow.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FollowRepository {

    private final JdbcTemplate jdbcTemplate;

    public FollowRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countFollowing(Long userId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE follower_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
        return count != null ? count : 0;
    }

    public int countFollowers(Long userId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE following_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);
        return count != null ? count : 0;
    }
}
