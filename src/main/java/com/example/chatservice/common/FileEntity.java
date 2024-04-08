package com.example.chatservice.common;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class FileEntity {
    @Column
    private String url;

    @Column
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column
    private String fileName;

    @Column
    private String uuid;

    @Column
    private LocalDateTime createdAt;
}
