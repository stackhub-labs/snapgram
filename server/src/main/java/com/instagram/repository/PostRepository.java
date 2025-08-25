package com.instagram.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public PostRepository(JdbcTemplate jdbcTemplate, LikeRepository likeRepository, CommentRepository commentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
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

    public void save(Long userId, String content, String imageUrl) {
        String sql = "INSERT INTO post (user_id, content, image_url) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, content, imageUrl);
    }

    public List<Map<String, Object>> findPostsByFollowingUsers(Long currentUserId, int offset, int limit) {
        String sql = """
            SELECT p.id, p.content, p.image_url, p.created_at, p.user_id,
                   u.name as username
            FROM post p
            JOIN user u ON p.user_id = u.id
            WHERE p.user_id IN (
                SELECT following_id 
                FROM follow 
                WHERE follower_id = ?
            )
            ORDER BY p.created_at DESC
            LIMIT ? OFFSET ?
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapToPostWithUser(rs, currentUserId), currentUserId, limit, offset);
    }

    public int countPostsByFollowingUsers(Long currentUserId) {
        String sql = """
            SELECT COUNT(*) 
            FROM post p
            WHERE p.user_id IN (
                SELECT following_id 
                FROM follow 
                WHERE follower_id = ?
            )
            """;
        
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, currentUserId);
        return count != null ? count : 0;
    }

    public boolean existsById(Long postId) {
        String sql = "SELECT COUNT(*) FROM post WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
        return count != null && count > 0;
    }

    private Map<String, Object> mapToPostWithUser(ResultSet rs, Long currentUserId) throws SQLException {
        Map<String, Object> post = new HashMap<>();
        post.put("id", rs.getLong("id"));
        post.put("content", rs.getString("content"));
        post.put("image_url", rs.getString("image_url"));
        post.put("created_at", rs.getString("created_at"));
        
        // 사용자 정보
        Map<String, Object> user = new HashMap<>();
        user.put("id", rs.getLong("user_id"));
        user.put("username", rs.getString("username"));
        post.put("user", user);
        
        // 좋아요 수와 현재 사용자의 좋아요 여부
        Long postId = rs.getLong("id");
        int likeCount = likeRepository.countLikesByPostId(postId);
        boolean isLiked = likeRepository.isLikedByUser(currentUserId, postId);
        
        post.put("like_count", likeCount);
        post.put("is_like", isLiked);
        post.put("comment_count", commentRepository.countCommentsByPostId(postId));
        
        return post;
    }

    public List<Map<String, Object>> findPostsByUserId(Long userId) {
        String sql = """
            SELECT p.id, p.content, p.image_url, p.created_at
            FROM post p
            WHERE p.user_id = ?
            ORDER BY p.created_at DESC
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapToPostSummary(rs, userId), userId);
    }

    private Map<String, Object> mapToPostSummary(ResultSet rs, Long userId) throws SQLException {
        Map<String, Object> post = new HashMap<>();
        post.put("id", rs.getLong("id"));
        post.put("content", rs.getString("content"));
        post.put("image_url", rs.getString("image_url"));
        post.put("created_at", rs.getString("created_at"));
        
        // 좋아요 수와 댓글 수
        Long postId = rs.getLong("id");
        int likeCount = likeRepository.countLikesByPostId(postId);
        int commentCount = commentRepository.countCommentsByPostId(postId);
        
        post.put("like_count", likeCount);
        post.put("comment_count", commentCount);
        
        return post;
    }

    public boolean deletePost(Long postId, Long userId) {
        // 게시물이 존재하고 해당 사용자의 것인지 확인
        String checkSql = "SELECT COUNT(*) FROM post WHERE id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, postId, userId);
        
        if (count == null || count == 0) {
            return false;
        }

        // 관련된 좋아요와 댓글 먼저 삭제
        likeRepository.deleteLikesByPostId(postId);
        commentRepository.deleteCommentsByPostId(postId);
        
        // 게시물 삭제
        String deleteSql = "DELETE FROM post WHERE id = ? AND user_id = ?";
        int deletedRows = jdbcTemplate.update(deleteSql, postId, userId);
        
        return deletedRows > 0;
    }
}
