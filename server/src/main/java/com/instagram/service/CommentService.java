package com.instagram.service;

import com.instagram.model.Comment;
import com.instagram.repository.CommentRepository;
import com.instagram.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public void createComment(Long postId, String content) {
        Long currentUserId = getCurrentAuthenticatedUserId();
        
        // 게시글이 존재하는지 확인
        if (!commentRepository.existsByPostId(postId)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }

        // 댓글 저장
        commentRepository.save(currentUserId, postId, content);
    }

    public void deleteComment(Long commentId) {
        Long currentUserId = getCurrentAuthenticatedUserId();
        
        // 댓글이 존재하는지 확인
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("댓글을 찾을 수 없습니다.");
        }

        // 댓글 작성자인지 확인
        Long commentUserId = commentRepository.getCommentUserId(commentId);
        if (!currentUserId.equals(commentUserId)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제
        commentRepository.deleteById(commentId);
    }

    public Map<String, Object> getCommentsByPostId(Long postId) {
        if (!commentRepository.existsByPostId(postId)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }

        List<Comment> comments = commentRepository.findByPostId(postId);
        
        int commentCount = commentRepository.countCommentsByPostId(postId);
        
        List<Map<String, Object>> commentList = comments.stream()
            .map(comment -> {
                var user = userRepository.findById(comment.getUserId());
                if (user == null) {
                    throw new IllegalStateException("댓글 작성자 정보를 찾을 수 없습니다.");
                }
                
                return Map.of(
                    "id", comment.getId(),
                    "user", Map.of(
                        "id", user.getId(),
                        "username", user.getNickname()
                    ),
                    "content", comment.getContent(),
                    "created_at", comment.getCreatedAt()
                );
            })
            .collect(Collectors.toList());
        
        return Map.of(
            "post_id", postId,
            "comments", commentList,
            "comment_count", commentCount
        );
    }

    private Long getCurrentAuthenticatedUserId() {
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("현재 HTTP 요청을 찾을 수 없습니다.");
        }

        HttpServletRequest request = attributes.getRequest();
        Long userId = (Long) request.getAttribute("userId");

        if (userId == null) {
            throw new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        return userId;
    }
} 