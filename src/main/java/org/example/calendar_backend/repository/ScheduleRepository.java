package org.example.calendar_backend.repository;

import org.example.calendar_backend.entity.Schedule;
import org.example.calendar_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 사용자의 일정 조회
    List<Schedule> findByUser(User user);

    // 친구의 공개된 일정만 조회하는 메서드
    List<Schedule> findByUserAndIsPublic(User user, boolean isPublic);
}
