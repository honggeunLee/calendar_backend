package org.example.calendar_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<UserDTO> register(@RequestBody SignUpRequestDTO signUpRequest) {
        UserDTO user = userService.register(signUpRequest);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "로그인", description = "사용자가 로그인하고 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequestDTO loginRequest) {
        JwtAuthenticationResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 요청 보내기", description = "로그인한 사용자가 친구에게 요청을 보냅니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/friends/request")
    public ResponseEntity<Void> sendFriendRequest(@RequestParam String friendEmail) {
        userService.sendFriendRequest(getAuthenticatedUserEmail(), friendEmail);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 요청 수락", description = "친구 요청을 수락합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/friends/accept")
    public ResponseEntity<Void> acceptFriendRequest(@RequestParam Long friendshipId) {
        userService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 목록 조회", description = "로그인한 사용자의 친구 목록을 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/friends")
    public ResponseEntity<List<UserDTO>> getFriends() {
        List<UserDTO> friends = userService.getFriends(getAuthenticatedUserEmail());
        return ResponseEntity.ok(friends);
    }

    @Operation(summary = "받은 친구 요청 목록 조회", description = "로그인한 사용자가 받은 친구 요청 목록을 조회합니다.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/friends/requests/received")
    public ResponseEntity<List<FriendRequestDTO>> getReceivedFriendRequests() {
        List<FriendRequestDTO> receivedRequests = userService.getReceivedFriendRequests(getAuthenticatedUserEmail());
        return ResponseEntity.ok(receivedRequests);
    }

    @Operation(summary = "친구 요청 거절", description = "친구 요청을 거절합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/friends/reject")
    public ResponseEntity<Void> rejectFriendRequest(@RequestParam Long friendshipId) {
        userService.rejectFriendRequest(friendshipId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "친구 삭제", description = "친구를 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/friends")
    public ResponseEntity<Void> removeFriend(@RequestParam String friendEmail) {
        userService.removeFriend(getAuthenticatedUserEmail(), friendEmail);
        return ResponseEntity.ok().build();
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
