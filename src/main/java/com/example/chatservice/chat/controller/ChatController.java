package com.example.chatservice.chat.controller;

import com.example.chatservice.chat.Service.ChatService;
import com.example.chatservice.chat.Service.SessionManager;
import com.example.chatservice.chat.dto.ChatRoomRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;
    private final SessionManager sessionManager;

    //채팅방 생성
    @PostMapping("/create")
    public void createRoom(@RequestBody ChatRoomRequestDTO chatRoomRequestDTO){
        chatService.creatRoom(chatRoomRequestDTO);
    }

    //사용자 세션이 세션 매니저에 존재하는지?
    @GetMapping("/session")
    public void IsSession(@RequestParam String username){
        System.out.println(sessionManager.getSession(username));
        //사용자가 로그인에 성공하면 특정 URI로 소켓 연결 시도 -> 해당 소켓은 로그아웃할때 까지 계속 지속
    }

    //채팅방에 사용자들 세션이 존재하는지?
    @GetMapping("/find-session")
    public void test(@RequestParam Long roomId){
        chatService.testSearch(roomId);
        //사용자가 로그인에 성공하면 특정 URI로 소켓 연결 시도 -> 해당 소켓은 로그아웃할때 까지 계속 지속
    }
}
