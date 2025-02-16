package org.example.calendar_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "일정 생성", description = "새로운 일정을 생성합니다.")
    @PostMapping
    public ResponseEntity<ScheduleDTO> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO createdSchedule = scheduleService.createSchedule(scheduleDTO);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    @Operation(summary = "일정 리스트 조회", description = "사용자의 일정 목록을 조회합니다.")
    @GetMapping("/user")
    public ResponseEntity<List<ScheduleDTO>> getSchedulesByUserEmail() {
        List<ScheduleDTO> schedules = scheduleService.getSchedulesByUserEmail();
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @Operation(summary = "일정 상세 조회", description = "특정 일정을 ID를 통해 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        ScheduleDTO schedule = scheduleService.getScheduleById(id);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @Operation(summary = "일정 수정", description = "기존 일정을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Long id, @RequestBody ScheduleDTO scheduleDTO) {
        ScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, scheduleDTO);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }

    @Operation(summary = "일정 삭제", description = "지정된 ID의 일정을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "친구의 일정 조회", description = "친구의 이메일을 통해 해당 친구의 일정을 조회합니다.")
    @GetMapping("/friend-schedules")
    public List<ScheduleDTO> getFriendSchedules(@RequestParam String friendEmail) {
        return scheduleService.getFriendSchedules(friendEmail);
    }
}
