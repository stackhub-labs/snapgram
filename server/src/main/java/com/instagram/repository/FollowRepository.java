package com.instagram.repository;

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
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public int countFollowers(Long userId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE following_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public void follow(Long followerId, Long followingId) {
        String sql = "INSERT INTO follow (follower_id, following_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, followerId, followingId);
    }

    public boolean isAlreadyFollowing(Long followerId, Long followingId) {
        String sql = "SELECT COUNT(*) FROM follow WHERE follower_id = ? AND following_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, followerId, followingId);
        return count != null && count > 0;
    }

    public void unfollow(Long followerId, Long followingId) {
        String sql = "DELETE FROM follow WHERE follower_id = ? AND following_id = ?";
        jdbcTemplate.update(sql, followerId, followingId);
    }
}
