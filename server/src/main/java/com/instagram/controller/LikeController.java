package com.instagram.controller;

import com.instagram.error.ErrorCode;
import com.instagram.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/like")
    public ResponseEntity<Object> toggleLike(@RequestBody Map<String, Long> request) {
        try {
            Long postId = request.get("post_id");
            if (postId == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of(
                        "code", ErrorCode.BAD_REQUEST,
                        "message", "post_id는 필수입니다."
                    ));
            }
            
            likeService.toggleLike(postId);
            return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "code", ErrorCode.BAD_REQUEST,
                    "message", e.getMessage()
                ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "code", ErrorCode.INTERNAL_SERVER_ERROR,
                    "message", "예상치 못한 오류가 발생했습니다."
                ));
        }
    }
} 