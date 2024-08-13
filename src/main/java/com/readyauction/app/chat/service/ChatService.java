package com.readyauction.app.chat.service;


import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.repository.ProductRepository;
import com.readyauction.app.chat.dto.*;
import com.readyauction.app.chat.entity.ChatMessage;
import com.readyauction.app.chat.entity.ChatRoom;
import com.readyauction.app.chat.entity.ChatRoomMember;
import com.readyauction.app.chat.entity.ChatRoomMemberStatus;
import com.readyauction.app.chat.repository.ChatMessageRepository;
import com.readyauction.app.chat.repository.ChatRoomRepository;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    // 채팅방 생성하기
    public void saveChatRooms(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        Optional<Member> seller = memberRepository.findById(product.get().getMemberId());
        Member memberSeller = seller.get();
        Optional<Member> winner = memberRepository.findById(product.get().getWinner().getMemberId());
        Member memberWinner = winner.get();
        ChatRoom chatRoom = ChatRoom.builder()
                .productId(productId)
                .chatRoomMembers(List.of(
                        new ChatRoomMember(memberSeller.getId(), memberSeller.getNickname(), ChatRoomMemberStatus.Seller),
                        new ChatRoomMember(memberWinner.getId(), memberWinner.getNickname(), ChatRoomMemberStatus.Winner)
                ))
                .build();
        chatRoomRepository.save(chatRoom);
    }

    public void saveMessages(MessageDto messageDto) {
        // 채팅 메시지 저장
        ChatMessage chatMessage = messageDto.toChatMessageEntity();
        chatMessageRepository.save(chatMessage);
        // 채팅방 마지막 메시지 & 시간 업데이트
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId());
        chatRoom.get().changeLastMessage(messageDto);
    }

    public MessageDto findAll() {
        return MessageDto.builder().build();
    }

    // 나의 채팅방 목록 조회
    public List<ChatRoomDto> findChatRoomsByMemberId(Long memberId) {

        Optional<List<ChatRoom>> chatRoomList = chatRoomRepository.findChatRoomsByMemberId(memberId);
        return chatRoomList.get().stream().map(ChatRoomDto::toChatRoomDto).toList();
    }

    public ChatRoomDto findChatRoomByProductId(Long productId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomByProductId(productId);
        return ChatRoomDto.toChatRoomDto(chatRoom.get());
    }

    // 채팅 메시지 내역 조회하기
    public List<MessageDto> findChatMessagesByChatRoomId(Long id) {
        Optional<List<ChatMessage>> chatMessageList = chatMessageRepository.findChatMessagesByChatRoomId(id);
        return chatMessageList.get().stream().map(MessageDto::toChatMessageDto).toList();
    }

    // 상품 조회하기
    public ChatProductDto findProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return ChatProductDto.toChatProductDto(product.get());
    }

    // 상품 거래자 프로필 조회하기
    public ChatProfileDto findMembers(Long sellerId, Long winnerId) {
        Optional<Member> seller = memberRepository.findById(sellerId);
        Optional<Member> winner = memberRepository.findById(winnerId);
        return ChatProfileDto.toChatProfileDto(seller.get(), winner.get());
    }

    public Long findChatRoomIdByProductId(Long productId) {
        Optional<Long> chatRoomId = chatRoomRepository.findChatRoomIdByProductId(productId);
        return chatRoomId.get();
    }

    // 모든 메시지 읽음 처리하기
    public void updateMessageStatus(Long chatRoomId) {
        Optional<List<ChatMessage>> chatMessageList = chatMessageRepository.findUnreadMessagesByChatRoomId(chatRoomId);
        chatMessageList.get().forEach((m) -> {
            m.changeStauts((short) 1);
        });
    }

    // 내가 받은(보내지 않은) 메시지 읽음 처리하기
    public void updateMessageStatus(Long chatRoomId, Long id) {
        Optional<List<ChatMessage>> chatMessageList = chatMessageRepository.findUnreadMessagesByChatRoomId(chatRoomId, id);
        chatMessageList.get().forEach((m) -> {
            m.changeStauts((short) 1);
        });
    }

    // 채팅방별 내가 안 읽은 메시지 개수 조회하기
    public List<ChatUnreadCountDto> findCountStatusByNotMemberId(Long memberId) {
        List<ChatUnreadCountDto> chatUnreadCountDtos = new ArrayList<>();
        Optional<List<Long>> chatRoomIdList = chatRoomRepository.findChatRoomIdsByMemberId(memberId);
        chatRoomIdList.get().stream().forEach((chatRoomId) -> {
            Optional<ChatUnreadCountDto> chatUnreadCountDto = chatMessageRepository.findUnreadCountsByNotMemberId(memberId, chatRoomId);
            chatUnreadCountDto.ifPresent(chatUnreadCountDtos::add);
            }
        );
//        Optional<ChatUnreadCountDto> unreadCounts = chatMessageRepository.findUnreadCountsByNotMemberId(memberId, chatRoomId);
        return chatUnreadCountDtos;
    }

    public List<Long> findChatRoomIdsByMemberId(Long id) {
        Optional<List<Long>> chatRoomIdList = chatRoomRepository.findChatRoomIdsByMemberId(id);
        return chatRoomIdList.get();
    }
}
