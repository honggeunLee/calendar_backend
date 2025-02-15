package org.example.calendar_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                         // 사용자 ID

    @Column(nullable = false, unique = true)
    private String email;                    // 로그인 ID

    @Column(nullable = false)
    private String password;                 // 비밀번호

    @Column(nullable = false)
    private String nickname;                 // 닉네임

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules;        // 사용자의 일정 목록

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Friendship> friendships;    // 친구 목록
}
