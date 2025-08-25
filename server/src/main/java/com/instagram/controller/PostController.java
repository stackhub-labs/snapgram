package com.instagram.controller;

import com.instagram.dto.PostRequest;
import com.instagram.error.ErrorCode;
import com.instagram.service.PostService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.address:0.0.0.0}")
    private String serverAddress;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/post")
    public ResponseEntity<Object> createPost(@RequestBody PostRequest request) {
        try {
            String imageUrl = request.getImageUrl();
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // 이미지 URL을 로컬에 저장하고 공유 가능한 URL로 변환
                System.out.println("원본 이미지 URL: " + imageUrl);
                String localImageUrl = saveImageLocally(imageUrl);
                System.out.println("변환된 이미지 URL: " + localImageUrl);
                if (localImageUrl != null) {
                    request.setImageUrl(localImageUrl);
                    System.out.println("PostRequest에 설정된 이미지 URL: " + request.getImageUrl());
                }
            }
            
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
    
    // 이미지 파일을 서버에 저장하고 공유 가능한 URL 반환
    private String saveImageLocally(String imageUrl) {
        try {
            // 이미지 저장 디렉토리 생성
            String uploadDir = "uploads/images";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 고유한 파일명 생성
            String extension = ".png"; // 기본 확장자
            if (imageUrl.contains(".")) {
                extension = imageUrl.substring(imageUrl.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // 실제 이미지 파일을 서버로 복사
            Path sourcePath = Paths.get(imageUrl);
            Path targetPath = Paths.get(uploadDir, filename);
            
            if (Files.exists(sourcePath)) {
                try {
                    // 파일을 서버의 uploads/images 디렉토리로 복사
                    System.out.println("파일 복사 시작: " + sourcePath + " -> " + targetPath);
                    Files.copy(sourcePath, targetPath);
                    System.out.println("파일 복사 성공!");
                    
                    // 공유 가능한 URL 반환 (서버 IP 사용)
                    String sharedUrl = "http://" + serverAddress + ":" + serverPort + "/images/" + filename;
                    System.out.println("생성된 공유 URL: " + sharedUrl);
                    return sharedUrl;
                } catch (Exception copyException) {
                    // 복사 실패 시 원본 경로를 그대로 반환 (테스트용)
                    System.out.println("파일 복사 실패: " + copyException.getMessage());
                    return imageUrl;
                }
            } else {
                // 파일이 존재하지 않으면 원본 경로 반환
                System.out.println("파일이 존재하지 않음: " + sourcePath);
                return imageUrl;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
