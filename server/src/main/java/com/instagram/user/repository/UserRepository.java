package com.instagram.user.repository;

import com.instagram.user.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{email}, Integer.class);
        return count != null && count > 0;
    }

    public boolean existsByNickname(String nickname) {
        String query = "SELECT COUNT(*) FROM user WHERE nickname = ?";
        Integer count = jdbcTemplate.queryForObject(query, new Object[]{nickname}, Integer.class);
        return count != null && count > 0;
    }

    public void save(User user) {
        String query = "INSERT INTO user (name, nickname, email, password, profile_image_url, info) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(query, user.getName(), user.getNickname(), user.getEmail(),
                user.getPassword(), user.getProfileImageUrl(), user.getInfo());
    }
}
