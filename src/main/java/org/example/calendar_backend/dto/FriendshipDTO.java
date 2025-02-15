package org.example.calendar_backend.dto;

import lombok.Data;
import org.example.calendar_backend.entity.FriendshipStatus;

@Data
public class FriendshipDTO {
    private Long id;
    private Long userId;
    private Long friendId;
    private FriendshipStatus status;
}
