package com.instagram.controller;

import com.instagram.dto.PostRequest;
import com.instagram.error.ErrorCode;
import com.instagram.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            return ResponseEntity.internalServerError().body(Map.of(
                    "code", ErrorCode.INTERNAL_SERVER_ERROR,
                    "message", "예상치 못한 오류가 발생했습니다."
            ));
        }
    }
}