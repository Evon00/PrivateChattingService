package com.example.chatservice.chat.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<MemberChatRoom> chatRooms = new HashSet<>();


}
