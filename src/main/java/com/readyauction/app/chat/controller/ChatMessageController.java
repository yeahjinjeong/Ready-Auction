package com.readyauction.app.chat.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.chat.dto.*;
import com.readyauction.app.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatMessageController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatService chatService;

    @GetMapping("/authentication/member")
    public ResponseEntity<?> getMemberId(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        return ResponseEntity.ok(principal.getMember());
    }

    @GetMapping("/message/{chatRoomId}")
    public List<MessageDto> findMessages(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        log.info("{}", chatRoomId);
        // chatRoomId로 메시지들을 찾는다
        List<MessageDto> messageDtos = chatService.findChatMessagesByChatRoomId(chatRoomId);
        log.info(messageDtos.toString());
        return messageDtos;
    }

    // chatRoomId의 모든 메시지들의 상태를 1 (읽음)으로 바꾼다
    // 비동기처리
    @PatchMapping("/message/{chatRoomId}/read")
    public void updateMessages(
            @PathVariable Long chatRoomId,
            @RequestParam Long anotherMemberId
    ) {
        log.info("senderId : {}", anotherMemberId); // 상대방 아이디
        // 채팅방 안 읽은 문자 읽기 - 어차피 상대방이 문자를 보냈다면, 내가 보낸 문자들을 다 읽었을 거고 나도 상대방 문자들을 다 읽은 거임
        chatService.updateMessageStatus(chatRoomId);
        // 내가 아닌 상대 멤버 찾기
        String userName = chatService.findEmailByMemberId(anotherMemberId);
        // 읽었다고 보낸 사람한테 알림 보내기
        simpMessagingTemplate.convertAndSendToUser(userName, "/sub", new MessageDto(null, chatRoomId, null, "enㅇtㅌrㄹy", null, null, null, null));
    }

    // 최초 접속시
    @GetMapping("/message/{chatRoomId}/entry")
    public void sendEntryMessage(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        // memberId가 나와 다르고 status가 0인 메시지들 읽기(1)
        chatService.updateMessageStatus(chatRoomId, principal.getMember().getId());

        // 내가 아닌 상대 멤버 찾기
        Long memberId = chatService.findOppositeMemberIdByChatRoomId(chatRoomId, principal.getMember().getId());
        String userName = chatService.findEmailByMemberId(memberId);


        simpMessagingTemplate.convertAndSendToUser(userName, "/sub", new MessageDto(null, chatRoomId, null, "enㅇtㅌrㄹy", null, null, null, null));
    }

    // 프로필 조회
    @GetMapping("/profile/{productId}")
    public ResponseEntity<?> findProfiles(@PathVariable Long productId
    ) {
        ChatProductDto chatProductDto = chatService.findProductById(productId);
        log.info("{}", chatProductDto);
        ChatProfileDto chatProfileDto = chatService.findMembers(chatProductDto.getSellerId(), chatProductDto.getWinnerId());
        log.info("{}", chatProfileDto);
        return ResponseEntity.ok(Map.of(
                "profile", chatProfileDto,
                "product", chatProductDto
        ));
    }

    // 읽지 않은 메시지 카운트 조회
    @GetMapping("/list/unread")
    public ResponseEntity<?> chatCountList(
            @AuthenticationPrincipal AuthPrincipal principal) {
        // 상대방이 보낸 메시지 중(멤버아이디가 다름) 0인 메시지 상태에 대해 카운트한다.
        List<ChatUnreadCountDto> countList = chatService.findCountStatusByNotMemberId(principal.getMember().getId());
        log.info("countList = {}", countList);
        return ResponseEntity.ok(countList);
    }

    // 상품 이미지 조회
//    @GetMapping("/list/image")
//    public ResponseEntity<?> findChatRoomImages(
//            @AuthenticationPrincipal AuthPrincipal principal) {
//        List<ChatImageDto> imageList = chatService.findImagesByChatRoomList(principal.getMember().getId());
//        log.info("imageList = {}", imageList);
//        return ResponseEntity.ok(imageList);
//    }
}