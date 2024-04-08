package com.example.chatservice.chat.dto;

import java.util.List;

public record ChatRoomRequestDTO(
        String username,
        String name,
        List<Long> members
) {
}
