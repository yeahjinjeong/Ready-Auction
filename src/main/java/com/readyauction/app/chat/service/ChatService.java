package com.readyauction.app.chat.service;


import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.repository.ProductRepository;
import com.readyauction.app.chat.dto.ChatProductDto;
import com.readyauction.app.chat.dto.ChatProfileDto;
import com.readyauction.app.chat.dto.ChatRoomDto;
import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.chat.entity.ChatMessage;
import com.readyauction.app.chat.entity.ChatRoom;
import com.readyauction.app.chat.repository.ChatMessageRepository;
import com.readyauction.app.chat.repository.ChatRoomRepository;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public void save(MessageDto messageDto) {
        ChatMessage chatMessage = messageDto.toChatMessageEntity();
        chatMessageRepository.save(chatMessage);
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId());
        chatRoom.get().changeLastMessage(messageDto);
    }

    public MessageDto findAll() {
        return MessageDto.builder().build();
    }

    public List<ChatRoomDto> findChatRoomsByMemberId(Long memberId) {

        Optional<List<ChatRoom>> chatRoomList = chatRoomRepository.findChatRoomsByMemberId(memberId);
        return chatRoomList.get().stream().map(ChatRoomDto::toChatRoomDto).toList();
    }

    public ChatRoomDto findChatRoomByProductId(Long productId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomByProductId(productId);
        return ChatRoomDto.toChatRoomDto(chatRoom.get());
    }

    public List<MessageDto> findChatMessagesByChatRoomId(Long id) {
        Optional<List<ChatMessage>> chatMessageList = chatMessageRepository.findChatMessagesByChatRoomId(id);
        return chatMessageList.get().stream().map(MessageDto::toChatMessageDto).toList();
    }

    public ChatProductDto findProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return ChatProductDto.toChatProductDto(product.get());
    }

    public ChatProfileDto findMembers(Long sellerId, Long winnerId) {
        Optional<Member> seller = memberRepository.findById(sellerId);
        Optional<Member> winner = memberRepository.findById(winnerId);
        return ChatProfileDto.toChatProfileDto(seller.get(), winner.get());
    }

    public Long findChatRoomIdByProductId(Long productId) {
        Optional<Long> chatRoomId = chatRoomRepository.findChatRoomIdByProductId(productId);
        return chatRoomId.get();
    }

    public void updateMessageStatus(Long chatRoomId) {
        Optional<List<ChatMessage>> chatMessageList = chatMessageRepository.findUnreadMessagesByChatRoomId(chatRoomId);
        chatMessageList.get().forEach((m) -> {
            m.changeStauts((short) 1);
        });
    }

    public void updateMessageStatus(Long chatRoomId, Long id) {
        Optional<List<ChatMessage>> chatMessageList = chatMessageRepository.findUnreadMessagesByChatRoomId(chatRoomId, id);
        chatMessageList.get().forEach((m) -> {
            m.changeStauts((short) 1);
        });
    }
}
