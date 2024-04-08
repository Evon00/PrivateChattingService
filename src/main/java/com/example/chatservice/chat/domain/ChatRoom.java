package com.example.chatservice.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column
    private String name;

    @Column
    private int count;

    @ManyToMany
    @JoinTable(name = "member_chat_room",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<Member> members = new HashSet<>();

    @Builder
    public ChatRoom(String name, RoomType roomType, int count){
        this.name = name;
        this.roomType = roomType;
        this.count = count;
    }

    public void setMembers(Set<Member> members){
        this.members = members;
    }

}