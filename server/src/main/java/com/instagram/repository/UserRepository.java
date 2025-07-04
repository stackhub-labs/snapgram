package com.instagram.repository;

import com.instagram.model.User;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setNickname(rs.getString("nickname"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setProfileImageUrl(rs.getString("profile_image_url"));
        user.setInfo(rs.getString("info"));
        user.setCreatedAt(rs.getString("created_at"));
        return user;
    };

    public boolean existsByEmail(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean existsByNickname(String nickname) {
        String query = "SELECT COUNT(*) FROM user WHERE nickname = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, nickname);
        return count != null && count > 0;
    }

    public void save(User user) {
        String query =
            "INSERT INTO user (name, nickname, email, password, profile_image_url, info) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(
            query,
            user.getName(),
            user.getNickname(),
            user.getEmail(),
            user.getPassword(),
            user.getProfileImageUrl(),
            user.getInfo()
        );
    }

    public User findByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(query, USER_ROW_MAPPER, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updatePassword(User user) {
        String query = "UPDATE user SET password = ? WHERE email = ?";
        jdbcTemplate.update(query, user.getPassword(), user.getEmail());
    }

    public List<User> findByNameOrNickname(String query) {
        String sql = "SELECT * FROM user WHERE name LIKE ? OR nickname LIKE ?";
        String likeQuery = "%" + query + "%";
        return jdbcTemplate.query(sql, USER_ROW_MAPPER, likeQuery, likeQuery);
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
