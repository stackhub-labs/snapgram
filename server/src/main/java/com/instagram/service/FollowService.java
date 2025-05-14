package com.instagram.service;

import com.instagram.error.ErrorCode;
import com.instagram.repository.FollowRepository;
import com.instagram.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public void followUser(Long userId) {
        // 현재 인증된 사용자의 ID는 실제 프로젝트에서는 Security Context에서 가져와야 함
        // 여기서는 임시로 하드코딩하거나 추후 Security 구현 후 수정 필요
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
    
    // 실제 구현에서는 Spring Security를 사용하여 현재 인증된 사용자의 ID를 가져와야 함
    private Long getCurrentAuthenticatedUserId() {
        // 임시 구현: 실제 구현 시 이 부분을 Security Context에서 정보를 가져오도록 변경해야 함
        return 1L; // 예시로 임시 값 반환
    }
}