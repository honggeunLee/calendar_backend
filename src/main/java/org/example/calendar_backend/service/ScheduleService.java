package org.example.calendar_backend.service;

import lombok.RequiredArgsConstructor;
import org.example.calendar_backend.dto.ScheduleDTO;
import org.example.calendar_backend.entity.Friendship;
import org.example.calendar_backend.entity.Schedule;
import org.example.calendar_backend.entity.User;
import org.example.calendar_backend.exception.InvalidOperationException;
import org.example.calendar_backend.exception.ResourceNotFoundException;
import org.example.calendar_backend.repository.FriendshipRepository;
import org.example.calendar_backend.repository.ScheduleRepository;
import org.example.calendar_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    /**
     * 로그인된 사용자의 이메일을 가져오는 메서드
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 현재 로그인한 사용자의 이메일
    }

    /**
     * 일정 생성
     */
    @Transactional
    public ScheduleDTO createSchedule(ScheduleDTO scheduleDTO) {
        // 현재 로그인된 사용자 이메일을 가져옴
        String currentUserEmail = getCurrentUserEmail();

        // User 존재 여부 확인
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. email: " + currentUserEmail));

        // Schedule 엔티티 생성
        Schedule schedule = Schedule.builder()
                .user(user)
                .title(scheduleDTO.getTitle())
                .description(scheduleDTO.getDescription())
                .startTime(scheduleDTO.getStartTime())
                .endTime(scheduleDTO.getEndTime())
                .isPublic(scheduleDTO.getIsPublic())
                .build();

        // 일정 저장
        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 저장된 스케줄을 DTO로 변환하여 반환
        return new ScheduleDTO(savedSchedule.getId(), savedSchedule.getTitle(), savedSchedule.getDescription(),
                savedSchedule.getStartTime(), savedSchedule.getEndTime(), savedSchedule.isPublic());
    }

    /**
     * 일정 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getSchedulesByUserEmail() {
        // 현재 로그인된 사용자 이메일을 가져옴
        String currentUserEmail = getCurrentUserEmail();

        // User 존재 여부 확인
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. email: " + currentUserEmail));

        // 사용자의 모든 일정 조회
        List<Schedule> schedules = scheduleRepository.findByUser(user);

        // Schedule 엔티티를 DTO로 변환하여 반환
        return schedules.stream().map(schedule -> new ScheduleDTO(schedule.getId(), schedule.getTitle(),
                        schedule.getDescription(), schedule.getStartTime(), schedule.getEndTime(), schedule.isPublic()))
                .collect(Collectors.toList());
    }

    /**
     * 일정 상세 조회
     */
    @Transactional(readOnly = true)
    public ScheduleDTO getScheduleById(Long id) {
        // 일정 존재 여부 확인
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("일정을 찾을 수 없습니다. id: " + id));

        // Schedule 엔티티를 DTO로 변환하여 반환
        return new ScheduleDTO(schedule.getId(), schedule.getTitle(), schedule.getDescription(),
                schedule.getStartTime(), schedule.getEndTime(), schedule.isPublic());
    }

    /**
     * 일정 수정
     */
    @Transactional
    public ScheduleDTO updateSchedule(Long id, ScheduleDTO scheduleDTO) {
        // 일정 존재 여부 확인
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("일정을 찾을 수 없습니다. id: " + id));

        // 수정된 값으로 업데이트
        schedule.setTitle(scheduleDTO.getTitle());
        schedule.setDescription(scheduleDTO.getDescription());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setPublic(scheduleDTO.getIsPublic());

        // 업데이트된 스케줄 저장
        Schedule updatedSchedule = scheduleRepository.save(schedule);

        // 수정된 스케줄을 DTO로 변환하여 반환
        return new ScheduleDTO(updatedSchedule.getId(), updatedSchedule.getTitle(),
                updatedSchedule.getDescription(), updatedSchedule.getStartTime(), updatedSchedule.getEndTime(), updatedSchedule.isPublic());
    }

    /**
     * 일정 삭제
     */
    @Transactional
    public void deleteSchedule(Long id) {
        // 일정 존재 여부 확인
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("일정을 찾을 수 없습니다. id: " + id));

        // 일정 삭제
        scheduleRepository.delete(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getFriendSchedules(String friendEmail) {
        // 현재 로그인된 사용자 이메일을 가져옴
        String currentUserEmail = getCurrentUserEmail();

        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. email: " + currentUserEmail));

        // 친구 존재 여부 확인
        User friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new ResourceNotFoundException("친구를 찾을 수 없습니다. email: " + friendEmail));

        // 친구가 수락한 친구 목록에 포함되어 있는지 확인
        Friendship friendship = friendshipRepository.findByUserAndFriend(user, friend)
                .orElseThrow(() -> new InvalidOperationException("친구가 아닙니다."));

        // 친구의 일정 조회 (공개된 일정만 조회)
        List<Schedule> schedules = scheduleRepository.findByUserAndIsPublic(friend, true); // 수정된 부분

        // Schedule 엔티티를 DTO로 변환하여 반환
        return schedules.stream().map(schedule -> new ScheduleDTO(schedule.getId(), schedule.getTitle(),
                        schedule.getDescription(), schedule.getStartTime(), schedule.getEndTime(), schedule.isPublic()))
                .collect(Collectors.toList());
    }

}
