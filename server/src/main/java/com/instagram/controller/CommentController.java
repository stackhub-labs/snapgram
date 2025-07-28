package com.instagram.controller;

import com.instagram.dto.CommentRequest;
import com.instagram.error.ErrorCode;
import com.instagram.service.CommentService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(value = "/comment/{post_id}")
    public ResponseEntity<Object> createComment(
            @PathVariable("post_id") Long postId,
            @RequestBody CommentRequest request) {
        try {
            commentService.createComment(postId, request.getContent());
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