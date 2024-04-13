package com.example.chatservice.chat.Service;

import com.example.chatservice.chat.domain.Member;
import com.example.chatservice.chat.dto.MessageRequestDTO;
import com.example.chatservice.chat.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfflineChatService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    //해당 사용자 오프라인시 redis에 메시지 저장
    public void saveOfflineMessage(String username, MessageRequestDTO messageRequestDTO) throws JsonProcessingException {
        String messageToJson = objectMapper.writeValueAsString(messageRequestDTO);
        redisTemplate.opsForList().rightPush(username,messageToJson);
    }

    //사용자 로그인시 오프라인때 받아온 모든 메시지 전달
    public List<MessageRequestDTO> getOfflineMessage(Long userId) throws JsonProcessingException{
        Optional<Member> member = memberRepository.findById(userId);
        List<String> messageJsonList = redisTemplate.opsForList().range(member.get().getUsername(),0,-1);
        return messageJsonList.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, MessageRequestDTO.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace(); // 예외 처리 필요
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    //메시지 전달 후 Redis에서 삭제
    public void deleteOfflineMessages(Long userId)
    {
        Optional<Member> member = memberRepository.findById(userId);
        redisTemplate.delete(member.get().getUsername());
    }
}
