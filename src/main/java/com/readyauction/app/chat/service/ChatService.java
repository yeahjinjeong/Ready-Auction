package com.readyauction.app.chat.service;


import com.readyauction.app.chat.dto.ChatRoomDto;
import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.chat.entity.ChatMessage;
import com.readyauction.app.chat.entity.ChatRoom;
import com.readyauction.app.chat.repository.ChatMessageRepository;
import com.readyauction.app.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public void save(MessageDto messageDto) {
        ChatMessage chatMessage = messageDto.toChatMessageEntity();
        chatMessageRepository.save(chatMessage);
    }

    public MessageDto findAll() {
        return MessageDto.builder().build();
    }

    public List<ChatRoomDto> findChatRoomsByMemberId(Long memberId) {

        List<ChatRoom> chatRoomList = chatRoomRepository.findChatRoomsByMemberId(memberId);
        return chatRoomList.stream().map(ChatRoomDto::toChatRoomDto).toList();
    }

    public ChatRoomDto findChatRoomByProductId(Long productId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByProductId(productId);
        return ChatRoomDto.toChatRoomDto(chatRoom);
    }

    public List<MessageDto> findChatMessagesByChatRoomId(Long id) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findChatMessagesByChatRoomId(id);
        return chatMessageList.stream().map(MessageDto::toChatMessageDto).toList();
    }
}
