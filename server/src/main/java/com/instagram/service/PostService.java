package com.instagram.service;

import com.instagram.dto.PostRequest;
import com.instagram.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void createPost(PostRequest request) {
        Long currentUserId = getCurrentAuthenticatedUserId();
        postRepository.save(currentUserId, request.getContent(), request.getImageUrl());
    }

    public Map<String, Object> getPostsByFollowingUsers(int page, int size) {
        Long currentUserId = getCurrentAuthenticatedUserId();
        
        int offset = (page - 1) * size;
        List<Map<String, Object>> posts = postRepository.findPostsByFollowingUsers(currentUserId, offset, size);
        int total = postRepository.countPostsByFollowingUsers(currentUserId);
        
        return Map.of(
            "posts", posts,
            "page", page,
            "size", size,
            "total", total
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
