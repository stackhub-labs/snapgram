package com.instagram.service;

import com.instagram.repository.LikeRepository;
import com.instagram.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    public void toggleLike(Long postId) {
        Long currentUserId = getCurrentAuthenticatedUserId();
        
        // 게시글이 존재하는지 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }

        // 이미 좋아요를 눌렀는지 확인
        boolean isLiked = likeRepository.isLikedByUser(currentUserId, postId);
        
        if (isLiked) {
            // 좋아요 취소
            likeRepository.unlike(currentUserId, postId);
        } else {
            // 좋아요 추가
            likeRepository.like(currentUserId, postId);
        }
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