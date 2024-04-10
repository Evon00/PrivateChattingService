package com.example.chatservice.chat.dto;

import java.util.List;

public record ChatRoomResponseDTO(
        Long roomId,
        String name,
        int count,

        List<String> members
) {
}
