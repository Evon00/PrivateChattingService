package com.example.chatservice.chat.dto;

public record ChatRoomResponseDTO(
        Long roomId,
        String name,
        int count,
        java.util.Set<com.example.chatservice.chat.domain.Member> members
) {
}
