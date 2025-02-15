package org.example.calendar_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // 일정 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;                  // 일정 소유자

    @Column(nullable = false)
    private String title;               // 일정 제목

    private String description;         // 일정 설명

    @Column(nullable = false)
    private LocalDateTime startTime;    // 시작 시간

    @Column(nullable = false)
    private LocalDateTime endTime;      // 종료 시간

    @Column(nullable = false)
    private boolean isPublic;           // 공개 여부 (true면 친구에게 공유)
}
