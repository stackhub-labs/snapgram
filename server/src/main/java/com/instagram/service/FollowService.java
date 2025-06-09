package com.instagram.service;

import com.instagram.error.ErrorCode;
import com.instagram.repository.FollowRepository;
import com.instagram.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public void followUser(Long userId) {
        Long currentUserId = getCurrentAuthenticatedUserId();
        
        if (userId.equals(currentUserId)) {
            throw new IllegalArgumentException("SELF_FOLLOW_NOT_ALLOWED:" + ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }
        
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException("USER_NOT_FOUND:" + ErrorCode.USER_NOT_FOUND);
        }
        
        if (followRepository.isAlreadyFollowing(currentUserId, userId)) {
            throw new IllegalArgumentException("ALREADY_FOLLOWING:" + ErrorCode.ALREADY_FOLLOWING);
        }
        
        followRepository.follow(currentUserId, userId);
    }

    public void unfollowUser(Long userId) {
        Long currentUserId = getCurrentAuthenticatedUserId();
        
        if (userId.equals(currentUserId)) {
            throw new IllegalArgumentException("SELF_FOLLOW_NOT_ALLOWED:" + ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
        }
        
        if (userRepository.findById(userId) == null) {
            throw new IllegalArgumentException("USER_NOT_FOUND:" + ErrorCode.USER_NOT_FOUND);
        }
        
        if (!followRepository.isAlreadyFollowing(currentUserId, userId)) {
            throw new IllegalArgumentException("NOT_FOLLOWING:" + ErrorCode.NOT_FOLLOWING);
        }
        
        followRepository.unfollow(currentUserId, userId);
    }
    
    private Long getCurrentAuthenticatedUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
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