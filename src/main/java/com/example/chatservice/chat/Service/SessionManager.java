package com.example.chatservice.chat.Service;

import com.example.chatservice.chat.domain.Member;
import com.example.chatservice.chat.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


//사용자가 로그인시에 서버에서 보관하고 있을 웹 소켓 세션을 관리하는 클래스
@Service
@RequiredArgsConstructor
public class SessionManager {
    private final Map<String, WebSocketSession> socketSessionSet = new ConcurrentHashMap<>();
    private final MemberRepository memberRepository;

    //JWT 대용으로 만들어 놓은 파라미터로 사용자 정보 입력
    public void addSessionWithId(Long userId, WebSocketSession session){
        Optional<Member> member = memberRepository.findById(userId);
        socketSessionSet.put(member.get().getUsername(), session);
    }

    public WebSocketSession getSession(String username){
        System.out.println("socketSessionSet에서 얻어온 결과입니다.");
        return socketSessionSet.get(username);
    }
}
