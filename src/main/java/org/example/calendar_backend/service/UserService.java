package org.example.calendar_backend.service;

import org.example.calendar_backend.dto.*;
import org.example.calendar_backend.entity.Friendship;
import org.example.calendar_backend.entity.FriendshipStatus;
import org.example.calendar_backend.entity.User;
import org.example.calendar_backend.exception.EmailAlreadyExistsException;
import org.example.calendar_backend.exception.InvalidOperationException;
import org.example.calendar_backend.exception.ResourceNotFoundException;
import org.example.calendar_backend.repository.FriendshipRepository;
import org.example.calendar_backend.repository.UserRepository;
import org.example.calendar_backend.security.JwtTokenProvider;
import org.example.calendar_backend.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       FriendshipRepository friendshipRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 회원가입
     */
    @Transactional
    public UserDTO register(SignUpRequestDTO signUpRequest) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("이미 사용 중인 이메일입니다: " + signUpRequest.getEmail());
        }

        // User 엔티티 생성 (비밀번호 암호화)
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .build();

        User savedUser = userRepository.save(user);

        // 엔티티를 DTO로 변환하여 반환
        return new UserDTO(savedUser.getId(), savedUser.getEmail(), savedUser.getNickname());
    }

    /**
     * 로그인: 인증 후 JWT 액세스 토큰과 리프레쉬 토큰 발급
     */
    @Transactional
    public JwtAuthenticationResponse login(LoginRequestDTO loginRequest) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        var authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // `UserPrincipal`로 변환하여 ID 가져오기
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateToken(userPrincipal.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal.getEmail());

        return new JwtAuthenticationResponse(accessToken, refreshToken);
    }

    /**
     * 친구 요청 보내기
     */
    @Transactional
    public void sendFriendRequest(String userEmail, String friendEmail) {
        // 자기 자신에게 요청하는지 검증
        if (userEmail.equals(friendEmail)) {
            throw new InvalidOperationException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        // 사용자 및 친구 존재 여부 확인
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. id: " + userEmail));
        User friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. id: " + friendEmail));

        // 이미 요청했거나 친구 관계가 존재하는지 확인
        if (friendshipRepository.findByUserAndFriend(user, friend).isPresent()) {
            throw new InvalidOperationException("이미 친구 요청을 보냈거나 친구 관계입니다.");
        }

        // 친구 요청 생성 (상태: PENDING)
        Friendship friendship = Friendship.builder()
                .user(user)
                .friend(friend)
                .status(FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(friendship);
    }

    /**
     * 친구 요청 수락 (양방향 친구 관계 생성)
     */
    @Transactional
    public void acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("친구 요청을 찾을 수 없습니다. id: " + friendshipId));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new InvalidOperationException("이미 처리된 친구 요청입니다.");
        }

        // 친구 요청 상태 변경
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        // 양방향 관계 관리를 위해 반대 방향 Friendship 객체가 없으면 생성
        if (friendshipRepository.findByUserAndFriend(friendship.getFriend(), friendship.getUser()).isEmpty()) {
            Friendship reverseFriendship = Friendship.builder()
                    .user(friendship.getFriend())
                    .friend(friendship.getUser())
                    .status(FriendshipStatus.ACCEPTED)
                    .build();
            friendshipRepository.save(reverseFriendship);
        }
    }

    /**
     * 친구 목록 조회 (수락된 친구 관계)
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getFriends(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. id: " + email));

        // 사용자가 보낸 친구 요청 중 수락된 목록 조회
        List<Friendship> friendships = friendshipRepository.findByUserAndStatus(user, FriendshipStatus.ACCEPTED);
        return friendships.stream().map(friendship -> {
            User friend = friendship.getFriend();
            return new UserDTO(friend.getId(), friend.getEmail(), friend.getNickname());
        }).collect(Collectors.toList());
    }

    /**
     * 받은 친구 요청 목록 조회 (PENDING 상태의 요청들)
     */
    @Transactional(readOnly = true)
    public List<FriendRequestDTO> getReceivedFriendRequests(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. email: " + email));

        List<Friendship> friendships = friendshipRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING);

        return friendships.stream().map(friendship -> {
            User friend = friendship.getUser();
            return new FriendRequestDTO(friendship.getId(), friend.getId(), friend.getEmail(), friend.getNickname());
        }).collect(Collectors.toList());
    }


    /**
     * 친구 요청 거절
     */
    @Transactional
    public void rejectFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("친구 요청을 찾을 수 없습니다. id: " + friendshipId));

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new InvalidOperationException("처리할 수 없는 친구 요청 상태입니다.");
        }

        // 친구 요청을 거절 상태로 변경
        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
    }

    /**
     * 친구 삭제
     */
    @Transactional
    public void removeFriend(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. email: " + userEmail));

        User friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. email: " + friendEmail));

        // 양방향 친구 관계를 삭제
        friendshipRepository.findByUserAndFriend(user, friend)
                .ifPresent(friendshipRepository::delete);

        friendshipRepository.findByUserAndFriend(friend, user)
                .ifPresent(friendshipRepository::delete);
    }

}
