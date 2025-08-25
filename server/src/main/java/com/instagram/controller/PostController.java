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
}
