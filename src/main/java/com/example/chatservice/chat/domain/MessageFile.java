package com.example.chatservice.chat.domain;

import com.example.chatservice.common.FileEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "message_file")
@Entity
@Getter
public class MessageFile extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}