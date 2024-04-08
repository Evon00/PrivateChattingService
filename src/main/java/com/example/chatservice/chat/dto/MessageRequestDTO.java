package com.example.chatservice.chat.dto;

import com.example.chatservice.chat.domain.MessageType;

import java.time.LocalDateTime;

public record MessageRequestDTO(

        Long chatRoom,

        String from,

        String message,

        LocalDateTime createdAt,

        MessageType messageType,

        Long subjectId
) {
}
