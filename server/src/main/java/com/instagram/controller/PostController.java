package com.instagram.controller;

import com.instagram.dto.PostRequest;
import com.instagram.error.ErrorCode;
import com.instagram.service.PostService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/post")
    public ResponseEntity<Object> createPost(@RequestBody PostRequest request) {
        try {
            postService.createPost(request);
            return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS));
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

    @GetMapping("/post")
    public ResponseEntity<Object> getPostsByFollowingUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Map<String, Object> response = postService.getPostsByFollowingUsers(page, size);
            return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS, "data", response));
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

    @GetMapping("/user/post")
    public ResponseEntity<Object> getPostsByUserId(@RequestParam("user_id") Long userId) {
        try {
            List<Map<String, Object>> posts = postService.getPostsByUserId(userId);
            return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS, "data", posts));
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

    @DeleteMapping("/post")
    public ResponseEntity<Object> deletePost(@RequestParam("post_id") Long postId) {
        try {
            boolean deleted = postService.deletePost(postId);
            if (deleted) {
                return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of(
                        "code", ErrorCode.BAD_REQUEST,
                        "message", "게시물을 찾을 수 없거나 삭제 권한이 없습니다."
                    ));
            }
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 에러 출력
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "code", ErrorCode.INTERNAL_SERVER_ERROR,
                    "message", "예상치 못한 오류가 발생했습니다: " + e.getMessage()
                ));
        }
    }
}
