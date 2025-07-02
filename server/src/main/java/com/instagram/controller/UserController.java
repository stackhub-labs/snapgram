package com.instagram.controller;

import com.instagram.dto.LoginRequest;
import com.instagram.dto.SignUpRequest;
import com.instagram.error.ErrorCode;
import com.instagram.model.User;
import com.instagram.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@Valid @RequestBody SignUpRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of("code", ErrorCode.SUCCESS)
            );
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.contains("Terms and conditions must be accepted")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                        "code",
                        ErrorCode.TERMS_NOT_ACCEPTED,
                        "message",
                        "이용약관에 동의해야 합니다."
                    )
                );
            } else if (message.contains("Invalid email format")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                        "code",
                        ErrorCode.INVALID_EMAIL_FORMAT,
                        "message",
                        "잘못된 이메일 형식입니다."
                    )
                );
            } else if (message.contains("Email already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                        "code",
                        ErrorCode.EMAIL_ALREADY_EXISTS,
                        "message",
                        "이미 사용 중인 이메일입니다."
                    )
                );
            } else if (message.contains("Nickname already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of(
                        "code",
                        ErrorCode.NICKNAME_ALREADY_EXISTS,
                        "message",
                        "이미 사용 중인 닉네임입니다."
                    )
                );
            } else if (message.contains("Invalid password format")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                        "code",
                        ErrorCode.INVALID_PASSWORD_FORMAT,
                        "message",
                        "비밀번호는 최소 8자 이상이어야 하며, 특수문자, 영문자, 숫자를 포함해야 합니다."
                    )
                );
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                    "code",
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "message",
                    "예상치 못한 오류가 발생했습니다."
                )
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> loginResult = userService.loginUser(request);
            return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS, "data", loginResult));
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.contains("Invalid email format")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                        "code",
                        ErrorCode.INVALID_EMAIL_FORMAT,
                        "message",
                        "잘못된 이메일 형식입니다."
                    )
                );
            } else if (message.contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                        "code",
                        ErrorCode.USER_NOT_FOUND,
                        "message",
                        "사용자를 찾을 수 없습니다."
                    )
                );
            } else if (message.contains("Password mismatch")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                        "code",
                        ErrorCode.PASSWORD_MISMATCH,
                        "message",
                        "비밀번호가 일치하지 않습니다."
                    )
                );
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                    "code",
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "message",
                    "예상치 못한 오류가 발생했습니다."
                )
            );
        }
    }

    @PostMapping("/find_password")
    public ResponseEntity<Object> findPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String tempPassword = userService.resetPassword(email);
            return ResponseEntity.ok()
                .body(
                    Map.of("code", ErrorCode.SUCCESS, "data", Map.of("temp_password", tempPassword))
                );
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                        "code",
                        ErrorCode.USER_NOT_FOUND,
                        "message",
                        "사용자를 찾을 수 없습니다."
                    )
                );
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                    "code",
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "message",
                    "예상치 못한 오류가 발생했습니다."
                )
            );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchUsers(@RequestParam(required = false) String query) {
        try {
            if (query == null || query.isBlank()) {
                return ResponseEntity.badRequest()
                    .body(
                        Map.of(
                            "code",
                            ErrorCode.BAD_REQUEST,
                            "message",
                            "검색어(query)가 필요합니다."
                        )
                    );
            }

            List<User> users = userService.searchUsersByNameOrNickname(query);
            List<Map<String, Object>> responseUsers = users
                .stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("username", user.getName());
                    map.put("nickname", user.getNickname());
                    map.put("profile_image_url", user.getProfileImageUrl());
                    return map;
                })
                .collect(Collectors.toList());

            if (responseUsers.isEmpty()) {
                return ResponseEntity.ok()
                    .body(
                        Map.of(
                            "code",
                            ErrorCode.NO_USERS_FOUND,
                            "data",
                            Map.of("users", List.of()),
                            "message",
                            "일치하는 사용자가 없습니다."
                        )
                    );
            }

            return ResponseEntity.ok()
                .body(Map.of("code", ErrorCode.SUCCESS, "data", Map.of("users", responseUsers)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                    "code",
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    "message",
                    "예상치 못한 오류가 발생했습니다."
                )
            );
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(@RequestParam("user_id") Long userId) {
        try {
            Map<String, Object> response = userService.getUserProfileById(userId);

            return ResponseEntity.ok().body(Map.of("code", ErrorCode.SUCCESS, "data", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("code", ErrorCode.USER_NOT_FOUND, "message", "사용자를 찾을 수 없습니다.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
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
