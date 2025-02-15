package org.example.calendar_backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.calendar_backend.dto.ScheduleDTO;
import org.example.calendar_backend.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 일정 생성
     */
    @PostMapping
    public ResponseEntity<ScheduleDTO> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO createdSchedule = scheduleService.createSchedule(scheduleDTO);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    /**
     * 일정 리스트 조회
     */
    @GetMapping("/user")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByUserEmail() {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByUserEmail();
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    /**
     * 일정 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        ScheduleDTO schedule = scheduleService.getScheduleById(id);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    /**
     * 일정 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Long id, @RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, scheduleDTO);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }

    /**
     * 일정 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 친구의 일정 조회
     */
    @GetMapping("/friend-schedules")
    public List<ScheduleDTO> getFriendSchedules(@RequestParam String friendEmail) {
        return scheduleService.getFriendSchedules(friendEmail);
    }
}
