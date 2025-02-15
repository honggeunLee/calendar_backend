package org.example.calendar_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                 // 친구 관계 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;               // 친구 요청을 보낸 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;             // 친구 요청을 받은 사용자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status; // 친구 상태 (PENDING, ACCEPTED, REJECTED)
}
