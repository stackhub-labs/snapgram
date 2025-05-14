package com.instagram.user.service;

import com.instagram.follow.repository.FollowRepository;
import com.instagram.post.repository.PostRepository;
import com.instagram.user.dto.LoginRequest;
import com.instagram.user.dto.SignUpRequest;
import com.instagram.user.model.User;
import com.instagram.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.instagram.config.JwtProperties;

import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProperties jwtProperties,
            PostRepository postRepository,
            FollowRepository followRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
        this.postRepository = postRepository;
        this.followRepository = followRepository;
    }

    public boolean registerUser(SignUpRequest request) {
        if (!request.isAgree()) {
            throw new IllegalArgumentException("Terms and conditions must be accepted");
        }

        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("Nickname already exists");
        }

        if (!isValidPassword(request.getPassword())) {
            throw new IllegalArgumentException("Password must contain at least one special character, one letter, and one number, and be at least 8 characters long.");
        }

        User user = request.toEntity();
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return true;
    }

    public String loginUser(LoginRequest request) {
        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password mismatch");
        }

        return generateToken(user);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("nickname", user.getNickname());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public String resetPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        String tempPassword = generateTempPassword();

        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.updatePassword(user);

        return tempPassword;
    }

    private String generateTempPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_+=<>?";
        String all = upper + lower + digits + special;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        for (int i = 4; i < 10; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        List<Character> pwdChars = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars, random);

        return pwdChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public List<User> searchUsersByNameOrNickname(String query) {
        return userRepository.findByNameOrNickname(query);
    }

    public Map<String, Object> getUserProfileById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        int postCount = postRepository.countByUserId(user.getId());
        int followingCount = followRepository.countFollowing(user.getId());
        int followerCount = followRepository.countFollowers(user.getId());
        List<Map<String, Object>> posts = postRepository.findPostSummariesByUserId(user.getId());

        Map<String, Object> userData = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "nickname", user.getNickname(),
                "email", user.getEmail(),
                "post_count", postCount,
                "following_count", followingCount,
                "follower_count", followerCount,
                "profile_image_url", Optional.ofNullable(user.getProfileImageUrl()).orElse("")
        );

        return Map.of("user", userData, "posts", posts);
    }
}
