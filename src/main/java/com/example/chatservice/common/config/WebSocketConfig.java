package com.example.chatservice.common.config;

import com.example.chatservice.chat.Service.ChatService;
import com.example.chatservice.chat.Service.SessionManager;
import com.example.chatservice.chat.repository.ChatRoomRepository;
import com.example.chatservice.common.handler.DynamicWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SessionManager sessionManager;
    private final ChatService chatService;

    //나중에 JWT에서 사용자 정보 얻어와 세션 저장? or 파라미터로

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new DynamicWebSocketHandler(sessionManager,chatService), "/ws/chat/{userId}").setAllowedOrigins("*");
    }
}
