package com.readyauction.app.chat.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.chat.dto.ChatRoomDto;
import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private final ChatService chatService;
    @MessageMapping("{memberId}") // pub/{chatRoomId}
//    @SendTo("/sub")
    public void chatting(
//            @PathVariable("memberId")
            @DestinationVariable("memberId")
            Long memberId,
            MessageDto messageDto) {
        log.info("message = {}", messageDto);
        log.info("memberId = {}", memberId);
        chatService.saveMessages(messageDto);
        String userName = chatService.findReceiverEmailByMemberId(messageDto.getReceiverId());
        String memberEmail = chatService.findReceiverEmailByMemberId(memberId);
        log.info("userName = {}", userName);
        log.info("memberEmail = {}", memberEmail);
        simpMessagingTemplate.convertAndSendToUser(userName, "/sub", messageDto);
//        simpMessagingTemplate.convertAndSendToUser(userName, "/sub", messageDto);
        simpMessagingTemplate.convertAndSendToUser(memberEmail, "/sub", messageDto);
//        return messageDto;
    }

    @ResponseBody
    @PostMapping("/chat/create")
    public ResponseEntity<?> createChatRoom(@RequestBody Long productId) {
        log.info("productId : {}", productId);
        chatService.saveChatRooms(productId);
        return ResponseEntity.ok("채팅방이 생성되었습니다");
    }

    @GetMapping("chat/list")
    public void chatList(
//            @PathVariable Long memberId,
            @AuthenticationPrincipal AuthPrincipal principal,
            Model model) {
        log.debug("멤버아이디 : {}", principal.getMember().getId());
        List<ChatRoomDto> chatRoomList  = chatService.findChatRoomsByMemberId(principal.getMember().getId());
//        List<ChatRoomDto> chatRoomList  = chatService.findChatRoomsByMemberId(0L);
        log.debug(chatRoomList.toString());
        model.addAttribute("chatRoomList", chatRoomList);
    }
}
