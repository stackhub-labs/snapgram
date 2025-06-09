package com.instagram.controller;

import com.instagram.dto.FollowRequest;
import com.instagram.error.ErrorCode;
import com.instagram.service.FollowService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/follow")
    public ResponseEntity<Object> followUser(@RequestBody FollowRequest request) {
        try {
            followService.followUser(request.getUserId());
            return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS));
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.contains("ALREADY_FOLLOWING")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                        "code",
                        ErrorCode.ALREADY_FOLLOWING,
                        "message",
                        "이미 팔로우 중인 사용자입니다."
                    )
                );
            } else if (message.contains("USER_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                        "code",
                        ErrorCode.USER_NOT_FOUND,
                        "message",
                        "사용자를 찾을 수 없습니다."
                    )
                );
            } else if (message.contains("SELF_FOLLOW_NOT_ALLOWED")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                        "code",
                        ErrorCode.SELF_FOLLOW_NOT_ALLOWED,
                        "message",
                        "자기 자신을 팔로우할 수 없습니다."
                    )
                );
            }
            return ResponseEntity.badRequest()
                .body(Map.of("code", ErrorCode.BAD_REQUEST, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(
                    Map.of(
                        "code",
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "message",
                        "예상치 못한 오류가 발생했습니다."
                    )
                );
        }
    }

    @DeleteMapping("/follow")
    public ResponseEntity<Object> unfollowUser(@RequestParam("user_id") Long userId) {
        try {
            followService.unfollowUser(userId);
            return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS));
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.contains("NOT_FOLLOWING")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                        "code",
                        ErrorCode.NOT_FOLLOWING,
                        "message",
                        "팔로우하지 않은 사용자입니다."
                    )
                );
            } else if (message.contains("USER_NOT_FOUND")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                        "code",
                        ErrorCode.USER_NOT_FOUND,
                        "message",
                        "사용자를 찾을 수 없습니다."
                    )
                );
            } else if (message.contains("SELF_FOLLOW_NOT_ALLOWED")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                        "code",
                        ErrorCode.SELF_FOLLOW_NOT_ALLOWED,
                        "message",
                        "자기 자신을 언팔로우할 수 없습니다."
                    )
                );
            }
            return ResponseEntity.badRequest()
                .body(Map.of("code", ErrorCode.BAD_REQUEST, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(
                    Map.of(
                        "code",
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "message",
                        "예상치 못한 오류가 발생했습니다."
                    )
                );
        }
    }
}
