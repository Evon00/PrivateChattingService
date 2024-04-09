package com.example.chatservice.chat.Service;

import com.example.chatservice.chat.domain.ChatRoom;
import com.example.chatservice.chat.domain.Member;
import com.example.chatservice.chat.domain.MessageType;
import com.example.chatservice.chat.dto.ChatRoomRequestDTO;
import com.example.chatservice.chat.dto.ChatRoomResponseDTO;
import com.example.chatservice.chat.dto.MessageRequestDTO;
import com.example.chatservice.chat.repository.ChatRoomRepository;
import com.example.chatservice.chat.domain.RoomType;
import com.example.chatservice.chat.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;
    private final Map<Long, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;


    public void creatRoom(ChatRoomRequestDTO chatRoomRequestDTO) {

        List<Long> members = chatRoomRequestDTO.members();
        members.add(memberRepository.findByUsername(chatRoomRequestDTO.username()).get().getId());
        ChatRoom chatRoom;

        //생성한 채팅방의 인원수가 2명 초과일 경우 그룹챗
        if(members.size() > 2){
            chatRoom = ChatRoom.builder()
                    .name(chatRoomRequestDTO.name().isEmpty() ? chatRoomRequestDTO.username() : chatRoomRequestDTO.name())
                    .roomType(RoomType.GROUP)
                    .count(members.size())
                    .build();
        } else { //생성한 채팅방의 인원수가 2명 이하일 경우 개인 챗
            chatRoom = ChatRoom.builder()
                    .name(chatRoomRequestDTO.name().isEmpty() ? chatRoomRequestDTO.username() : chatRoomRequestDTO.name())
                    .roomType(RoomType.PRIVATE)
                    .count(members.size())
                    .build();
        }

        Set<Member> memberSet = new HashSet<>();

        // chatRoom 도메인에 사용자 추가 과정
        for(Long memberId : members){
            Member member = memberRepository.findById(memberId).get();
            memberSet.add(member);
        }

        chatRoom.setMembers(memberSet);
        ChatRoom save = chatRoomRepository.save(chatRoom);

        // chatRoom 도메인에 사용자 추가 과정

        //원래는 이렇게 찾아야함

        Set<WebSocketSession> sessions = new HashSet<>();
        for(Member member : save.getMembers()){
            sessions.add(sessionManager.getSession(member.getUsername()));
        }

        //

        //테스트 버전 -> 현재 웹 소켓 세션에서 정보 가져올때 사용자 정보를 못가져와서...
        //WebSocketSession test1 = sessionManager.getSession("test1");
        //Set<WebSocketSession> sessions = new HashSet<>();
        //sessions.add(test1);
        //테스트 버전

        chatRooms.put(save.getId(), sessions);

    }

    //채팅방에 소켓 세션 잘 들어가있는지 테스트
    public void testSearch(Long roomId){
        System.out.println(chatRooms.get(roomId));
    }

    public ChatRoom findRoomByName(String name){
        Optional<ChatRoom> room = chatRoomRepository.findByName(name);
        return room.get();
    }

    public List<ChatRoomResponseDTO> findAllRoom(){
        List<ChatRoomResponseDTO> response = new ArrayList<>(); // 비어 있는 리스트로 초기화

        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        // 각 ChatRoom을 ChatRoomResponseDTO로 변환하여 response 리스트에 추가
        chatRoomList.forEach(chatRoom -> response.add(new ChatRoomResponseDTO(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getCount(),
                chatRoom.getMembers()
        )));

        return response;
    }

    //채팅방에 있는 세션들에게 전부 메시지 전송
    //웹 소켓은 언제 사라지는지? 사라졌으면 다시 어떻게 해야하는지?
    public void sendMessage(WebSocketMessage<?> message) throws IOException {

        Object payload = message.getPayload();
        MessageRequestDTO messageDTO = objectMapper.readValue(payload.toString(),MessageRequestDTO.class);
        Set<WebSocketSession> sessions = chatRooms.getOrDefault(messageDTO.chatRoom(), Collections.emptySet());


        if(messageDTO.messageType().equals(MessageType.CHAT)){
            for(WebSocketSession session : sessions){
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            }
        }

    }

}
