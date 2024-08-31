package com.readyauction.app.chat.service;


import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.service.ProductService;
import com.readyauction.app.chat.dto.*;
import com.readyauction.app.chat.entity.ChatMessage;
import com.readyauction.app.chat.entity.ChatRoom;
import com.readyauction.app.chat.entity.ChatRoomMember;
import com.readyauction.app.chat.entity.ChatRoomMemberStatus;
import com.readyauction.app.chat.repository.ChatMessageRepository;
import com.readyauction.app.chat.repository.ChatRoomRepository;
import com.readyauction.app.common.handler.ChatNotFoundException;
import com.readyauction.app.user.entity.Member;
import com.readyauction.app.user.service.MemberService;
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

    private final ProductService productService;
    private final MemberService memberService;

//    private final ProductRepository productRepository;
//    private final MemberRepository memberRepository;

    // 채팅방 생성하기
    public void saveChatRooms(Long productId) {
//        Optional<Product> product = productRepository.findById(productId);
        Optional<Product> product = productService.findById(productId);

//        Optional<Member> seller = memberRepository.findById(product.get().getMemberId());
//        Member memberSeller = seller.get();
        Member seller = memberService.findById(product.get().getMemberId());

//        Optional<Member> winner = memberRepository.findById(product.get().getWinner().getMemberId());
//        Member memberWinner = winner.get();
        Member winner = memberService.findById(product.get().getWinner().getMemberId());
        ChatRoom chatRoom = ChatRoom.builder()
                .productId(productId)
                .chatRoomMembers(List.of(
                        new ChatRoomMember(seller.getId(), seller.getNickname(), ChatRoomMemberStatus.Seller),
                        new ChatRoomMember(winner.getId(), winner.getNickname(), ChatRoomMemberStatus.Winner)
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

    // 채팅방 + 상품 목록 조회
    public List<ChatRoomProductDto> findChatRoomAndProductByMemberId(Long memberId) {
        Optional<List<ChatRoomProductDto>> chatRoomAndProductByMemberIds = chatRoomRepository.findChatRoomAndProductByMemberId(memberId);
        return chatRoomAndProductByMemberIds.get();
    }

    // 채팅 메시지 내역 조회하기
    public List<MessageDto> findChatMessagesByChatRoomId(Long id) {
        Optional<List<ChatMessage>> chatMessageList = chatMessageRepository.findChatMessagesByChatRoomId(id);
        return chatMessageList.get().stream().map(MessageDto::toChatMessageDto).toList();
    }

    // 상품 조회하기
    public ChatProductDto findProductById(Long productId) {
//        Optional<Product> product = productRepository.findById(productId);
        Optional<Product> product = productService.findById(productId);
        return ChatProductDto.toChatProductDto(product.get());
    }

    // 상품 거래자 프로필 조회하기
    public ChatProfileDto findMembers(Long sellerId, Long winnerId) {
//        Optional<Member> seller = memberRepository.findById(sellerId);
        Member seller = memberService.findById(sellerId);
//        Optional<Member> winner = memberRepository.findById(winnerId);
        Member winner = memberService.findById(winnerId);
        return ChatProfileDto.toChatProfileDto(seller, winner);
    }

    public Long findChatRoomIdByProductId(Long productId) {
        Optional<Long> chatRoomId = chatRoomRepository.findChatRoomIdByProductId(productId);
        return chatRoomId.get();
    }

    public ChatRoomDto findChatRoomByProductId(Long productId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findChatRoomByProductId(productId);
        return ChatRoomDto.toChatRoomDto(chatRoom.get());
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
        // 안 읽은 채팅 개수 리스트
        List<ChatUnreadCountDto> chatUnreadCountDtos = new ArrayList<>();
        // 내가 참여하고 있는 채팅방 아이디 리스트
        List<Long> chatRoomIdList = chatRoomRepository.findChatRoomIdsByMemberId(memberId).orElseThrow(() -> new ChatNotFoundException("chatroom not found for user ID: " + memberId));
        if (!chatRoomIdList.isEmpty()) {
            chatRoomIdList.forEach((chatRoomId) -> {
                Optional<ChatUnreadCountDto> chatUnreadCountDto = chatMessageRepository.findUnreadCountsByNotMemberId(memberId, chatRoomId);
                chatUnreadCountDto.ifPresent(chatUnreadCountDtos::add);
                }
            );
        }
        return chatUnreadCountDtos;
    }

    // convertAndSendToUser 유니캐스트를 위해 식별값 조회
    public String findReceiverEmailByMemberId(Long receiverId) {
//        Optional<Member> member = memberRepository.findById(receiverId);
        return memberService.findEmailById(receiverId);
    }

    // 채팅방 상품 사진 조회
    public List<ChatImageDto> findImagesByChatRoomList(Long memberId) {
        // 경매 상품 리스트의 이미지 리스트 중 첫번째거를 받아서 조회해야 함
        // 경매 테이블이랑 이미지 테이블 조인해서 멤버 아이디에 맞는 이미지들 전부 불러와서 첫번째것만 어떻게 불러오지?
        List<ChatImageDto> chatImageDtos = new ArrayList<>();
        List<Long> productList = chatRoomRepository.findProductIdsByMemberId(memberId).orElseThrow(() -> new ChatNotFoundException("chatroom not found for user ID: " + memberId));
        if (!productList.isEmpty()) {
            productList.forEach((productId) -> {
//            Optional<List<String>> images = productRepository.findImagesById(productId);
                List<String> images = productService.findImagesById(productId);
                chatImageDtos.add(new ChatImageDto(productId, images.get(0)));
            });
        }
        return chatImageDtos;
    }

    public Long findOppositeMemberIdByChatRoomId(Long chatRoomId, Long id) {
        Long memberId = chatRoomRepository.findOppositeMemberIdByChatRoomId(chatRoomId, id);
        return memberId;
    }
}
