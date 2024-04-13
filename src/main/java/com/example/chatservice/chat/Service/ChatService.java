package com.example.chatservice.chat.Service;

import com.example.chatservice.chat.domain.*;
import com.example.chatservice.chat.dto.ChatRoomRequestDTO;
import com.example.chatservice.chat.dto.ChatRoomResponseDTO;
import com.example.chatservice.chat.dto.MessageRequestDTO;
import com.example.chatservice.chat.repository.ChatRoomRepository;
import com.example.chatservice.chat.repository.MemberRepository;
import com.example.chatservice.chat.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;
    private final MessageRepository messageRepository;
    private final OfflineChatService offlineChatService;
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

        //chatRoom 도메인에 사용자 추가 과정

        Set<MemberChatRoom> memberChatRoomSet = new HashSet<>();

        for(Long memberId : members){
            Member member = memberRepository.findById(memberId).get();
            MemberChatRoom memberChatRoom =
                    MemberChatRoom.builder()
                            .chatRoom(chatRoom)
                            .member(member)
                            .read(false)
                            .build();
            memberChatRoomSet.add(memberChatRoom);
        }

        chatRoom.setMembers(memberChatRoomSet);

        chatRoomRepository.save(chatRoom);
        // chatRoom 도메인에 사용자 추가 과정


    }

    public ChatRoom findRoomByName(String name){
        Optional<ChatRoom> room = chatRoomRepository.findByName(name);
        return room.get();
    }

    public List<ChatRoomResponseDTO> findAllRoom(){
        List<ChatRoomResponseDTO> response = new ArrayList<>(); // 비어 있는 리스트로 초기화

        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();
        // 각 ChatRoom을 ChatRoomResponseDTO로 변환하여 response 리스트에 추가
        chatRoomList.forEach(chatRoom -> {
            List<String> members = new ArrayList<>();
            for (MemberChatRoom member : chatRoom.getMembers()) {
                members.add(member.getMember().getUsername());
            }
            response.add(new ChatRoomResponseDTO(
                    chatRoom.getId(),
                    chatRoom.getName(),
                    chatRoom.getCount(),
                    members
            ));
        });

        return response;
    }

    // 채팅방에 있는 사용자 세션을 세션 매니저에서 검색 후 메시지 전송
    // 세션이 있는 경우 정상 작동, 세션이 없는 경우 임시 메시지 저장소로 전송
    public void sendMessage(WebSocketMessage<?> message) throws IOException {

        //메시지 객체 변환
        Object payload = message.getPayload();
        MessageRequestDTO messageDTO = objectMapper.readValue(payload.toString(),MessageRequestDTO.class);
        //메시지 객체 변환

        //메시지 내용중 채팅방 번호 확인 및 사용자 검색
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(messageDTO.chatRoom());
        Set<MemberChatRoom> memberChatRoomSet = chatRoom.get().getMembers();
        //메시지 내용중 채팅방 번호 확인 및 사용자 검색

        if(messageDTO.messageType().equals(MessageType.CHAT)){
                for(MemberChatRoom member : memberChatRoomSet){
                    WebSocketSession session = sessionManager.getSession(member.getMember().getUsername());
                    if(session == null){ //현재 채팅방에는 사람이 있으나, 세션이 없는 경우 = 로그아웃 , 메시지 임시 저장소에 저장
                        offlineChatService.saveOfflineMessage(member.getMember().getUsername(), messageDTO);
                        System.out.println("해당 사용자 로그아웃상태라 redis에 저장해둘게요");
                    } else { //현재 채팅방에 있고, 세션이 있는 경우 = 로그인 , 메시지 전송
                        TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(messageDTO));
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(textMessage)));
                    }
                }
        }
    }
}
