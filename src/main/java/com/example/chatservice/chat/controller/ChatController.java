package com.example.chatservice.chat.controller;

import com.example.chatservice.chat.Service.ChatService;
import com.example.chatservice.chat.Service.SessionManager;
import com.example.chatservice.chat.dto.ChatRoomRequestDTO;
import com.example.chatservice.chat.dto.ChatRoomResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    //삭제 예정
    @GetMapping("/session")
    public void IsSession(@RequestParam String username){
        System.out.println(sessionManager.getSession(username));
        //사용자가 로그인에 성공하면 특정 URI로 소켓 연결 시도 -> 해당 소켓은 로그아웃할때 까지 계속 지속
    }

    //모든 채팅방 리스트 조회
    @GetMapping("/list")
    public List<ChatRoomResponseDTO> getChatList(){
        return chatService.findAllRoom();
    }

}
