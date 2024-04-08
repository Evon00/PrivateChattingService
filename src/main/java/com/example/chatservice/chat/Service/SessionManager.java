package com.example.chatservice.chat.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//사용자가 로그인시에 서버에서 보관하고 있을 웹 소켓 세션을 관리하는 클래스
@Service
public class SessionManager {
    private final Map<String, WebSocketSession> socketSessionSet = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session){

        System.out.println("here is session manager : "+ session.getId());
        socketSessionSet.put("test1",session); //test1에 사용자 아이디 들어가서 key 역할
    }

    public WebSocketSession getSession(String username){
        System.out.println("socketSessionSet에서 얻어온 결과입니다.");
        return socketSessionSet.get(username);
    }
}
