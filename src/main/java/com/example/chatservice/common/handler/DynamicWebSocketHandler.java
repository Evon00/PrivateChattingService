package com.example.chatservice.common.handler;

import com.example.chatservice.chat.Service.ChatService;
import com.example.chatservice.chat.Service.OfflineChatService;
import com.example.chatservice.chat.Service.SessionManager;
import com.example.chatservice.chat.dto.MessageRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.WebSocketHandler;

import java.net.URI;
import java.util.List;


@Component
@RequiredArgsConstructor
public class DynamicWebSocketHandler implements WebSocketHandler {

    private final SessionManager sessionManager;
    private final ChatService chatService;
    private final OfflineChatService offlineChatService;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String URI = String.valueOf(session.getUri());
        String segment[] = URI.split("/");
        Long userId = Long.valueOf(segment[segment.length-1]);

        if(userId != null){
            sessionManager.addSessionWithId(userId,session); //세션 등록
            List<MessageRequestDTO> offlineMessages = offlineChatService.getOfflineMessage(userId);//오프라인때 온 메시지가 있는지 확인
            if(offlineMessages.isEmpty()){
                session.sendMessage(new TextMessage("connected:"+session.getId()));
            } else {
                for(MessageRequestDTO message : offlineMessages) {
                    TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));
                    session.sendMessage(textMessage);
                }
                offlineChatService.deleteOfflineMessages(userId);
            }
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
    //세션 매니저에 세션 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

        String URI = String.valueOf(session.getUri());
        String segment[] = URI.split("/");
        Long userId = Long.valueOf(segment[segment.length-1]);
        sessionManager.setSessionNull(userId);
        System.out.println("접속 종료 완료");
    }

    //대용량 메시지 처리 방식
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
