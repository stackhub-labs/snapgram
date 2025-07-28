package com.instagram.service;

import com.instagram.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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