package com.readyauction.app.chat.controller;

import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.chat.dto.*;
import com.readyauction.app.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatService chatService;

    @GetMapping("/api/authentication/member")
    public ResponseEntity<?> getMemberId(
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        return ResponseEntity.ok(principal.getMember());
    }

    @GetMapping("chat/message/{chatRoomId}")
    public List<MessageDto> findMessages(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        // productId로 chatRoomId를 찾아서
//        ChatRoomDto chatRoomDto = chatService.findChatRoomByProductId(productId);
//        Long chatRoomId = chatService.findChatRoomIdByProductId(productId);
        // chatRoomId로 메시지들을 찾는다
//        List<MessageDto> messageDtos = chatService.findChatMessagesByChatRoomId(chatRoomDto.getId());
        log.info("{}", chatRoomId);
        // memberId가 나와 다르고 status가 0인 메시지들 읽기(1)
        chatService.updateMessageStatus(chatRoomId, principal.getMember().getId());
//        chatService.updateMessageStatus(chatRoomId, 1L);
        // chatRoomId로 메시지들을 찾는다
        List<MessageDto> messageDtos = chatService.findChatMessagesByChatRoomId(chatRoomId);
        log.info(messageDtos.toString());
        return messageDtos;
    }

    // chatRoomId의 모든 메시지들의 상태를 1 (읽음)으로 바꾼다
    @PostMapping("/chat/message/{chatRoomId}/read")
    public void updateMessages(
            @PathVariable Long chatRoomId
    ) {
        log.info("읽었냐고요!");
        chatService.updateMessageStatus(chatRoomId);
    }

    @GetMapping("chat/profile/{productId}")
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

    @GetMapping("/chat/list/count")
    public ResponseEntity<?> chatCountList(
            @AuthenticationPrincipal AuthPrincipal principal) {
        // 상대방이 보낸 메시지 중(멤버아이디가 다름) 0인 메시지 상태에 대해 카운트한다.
        List<ChatUnreadCountDto> countList = chatService.findCountStatusByNotMemberId(principal.getMember().getId());
        log.info("countList = {}", countList);
        return ResponseEntity.ok(countList);
    }
}