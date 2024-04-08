package com.example.chatservice.chat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashSet;
import java.util.Set;

@Getter
@RedisHash(value = "redisChatRoom")
@NoArgsConstructor
public class RedisChatRoom {

    @Id
    private String chatRoomId;

    private Set<String> sessions = new HashSet<>();

    public RedisChatRoom(String roomId, String session) {
        this.chatRoomId = roomId;
        this.sessions.add(session);
    }
}
