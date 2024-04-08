package com.example.chatservice.chat.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String name;

    @ManyToMany(mappedBy = "members")
    private Set<ChatRoom> chatRooms = new HashSet<>();


}
