package com.example.chatservice.common.handler;

import com.example.chatservice.chat.Service.ChatService;
import com.example.chatservice.chat.Service.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.WebSocketHandler;


@Component
@RequiredArgsConstructor
public class DynamicWebSocketHandler implements WebSocketHandler {

    private final SessionManager sessionManager;
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String URI = String.valueOf(session.getUri());
        String segment[] = URI.split("/");
        Long userId = Long.valueOf(segment[segment.length-1]);

        if(userId != null){
            sessionManager.addSessionWithId(userId,session);
            session.sendMessage(new TextMessage("connected: "+session.getId()));
        }
        else {
            System.out.println("파라미터 오류 or JWT 오류");
        }
    }


    //메시지 전송
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        chatService.sendMessage(message);
    }


    //에러 처리
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("error");
    }

    //소켓 접속 끊은 후
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("quit");
    }

    //대용량 메시지 처리 방식
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
