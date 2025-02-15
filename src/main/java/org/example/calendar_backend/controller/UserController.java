package org.example.calendar_backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.calendar_backend.dto.*;
import org.example.calendar_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> register(@RequestBody SignUpRequestDTO signUpRequest) {
        UserDTO user = userService.register(signUpRequest);
        return ResponseEntity.ok(user);
    }

    /**
     * 로그인 API (JWT 발급)
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequestDTO loginRequest) {
        JwtAuthenticationResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 요청 보내기
     */
    @PreAuthorize("isAuthenticated()") // 로그인한 사용자만 요청 가능
    @PostMapping("/friends/request")
    public ResponseEntity<Void> sendFriendRequest(@RequestParam String friendEmail) {
        userService.sendFriendRequest(getAuthenticatedUserEmail(), friendEmail);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 요청 수락
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/friends/accept")
    public ResponseEntity<Void> acceptFriendRequest(@RequestParam Long friendshipId) {
        userService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 목록 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/friends")
    public ResponseEntity<List<UserDTO>> getFriends() {
        List<UserDTO> friends = userService.getFriends(getAuthenticatedUserEmail());
        return ResponseEntity.ok(friends);
    }

    /**
     * 현재 로그인한 사용자 ID 가져오기
     */
    private String getAuthenticatedUserEmail() {
        // SecurityContextHolder에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();  // username(이메일)을 그대로 반환
    }

    /**
     * 받은 친구 요청 목록 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/friends/requests/received")
    public ResponseEntity<List<FriendRequestDTO>> getReceivedFriendRequests() {
        List<FriendRequestDTO> receivedRequests = userService.getReceivedFriendRequests(getAuthenticatedUserEmail());
        return ResponseEntity.ok(receivedRequests);
    }

    /**
     * 친구 요청 거절
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/friends/reject")
    public ResponseEntity<Void> rejectFriendRequest(@RequestParam Long friendshipId) {
        userService.rejectFriendRequest(friendshipId);
        return ResponseEntity.ok().build();
    }

    /**
     * 친구 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/friends")
    public ResponseEntity<Void> removeFriend(@RequestParam String friendEmail) {
        userService.removeFriend(getAuthenticatedUserEmail(), friendEmail);
        return ResponseEntity.ok().build();
    }
}
