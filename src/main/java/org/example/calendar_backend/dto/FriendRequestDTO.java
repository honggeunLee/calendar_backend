package org.example.calendar_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestDTO {
    private Long friendshipId;
    private Long userId;
    private String email;
    private String nickname;
}
