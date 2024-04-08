package com.example.chatservice.common.handler;

import com.example.chatservice.chat.Service.SessionManager;
import com.example.chatservice.chat.domain.ChatRoom;
import com.example.chatservice.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DynamicWebSocketHandler implements WebSocketHandler {

    private final ChatRoomRepository chatRoomRepository;
    private final SessionManager sessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String URI = String.valueOf(session.getUri());
        String segment[] = URI.split("/");
        Long roomId = Long.valueOf(segment[segment.length-1]);

        //대기실 느낌? 로그인 성공하면 서버에 소켓 세션 저장하기
        if(roomId == 0){
            sessionManager.addSession(session);
            session.sendMessage(new TextMessage("connected: "+session.getId()));
            //sessionManager.addSession(session,Member member);
            //이후에 JWT와 연동했을 경우 사용자 정보 파라미터로..
        } else {
            Optional<ChatRoom> byId = chatRoomRepository.findById(roomId);

            if(byId.isEmpty()){
                System.out.println("No Room");
                session.close(CloseStatus.NO_STATUS_CODE);
            } else{
                session.sendMessage(new TextMessage("connected : "+session.getId()));
            }
        }
    }


    //메시지 전송
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        /*
        Object payload = message.getPayload();
        MessageRequestDTO message1 = objectMapper.readValue((JsonParser) payload,MessageRequestDTO.class);
        if(message1.messageType().equals(MessageType.ENTER)){
            chatService.addSessionToRoom(message1.chatRoom(),session);
            System.out.println("메시지 전송");
        } else {
            chatService.sendMessage(message1.chatRoom(),message1);
        }*/
        
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
