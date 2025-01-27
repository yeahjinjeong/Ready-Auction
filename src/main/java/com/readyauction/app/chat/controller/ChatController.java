package com.readyauction.app.chat.controller;

import com.readyauction.app.auth.principal.AuthPrincipal;
import com.readyauction.app.chat.dto.ChatRoomProductDto;
import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
//    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;

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

        String receiverEmail = chatService.findEmailByMemberId(messageDto.getReceiverId());
        String senderEmail = chatService.findEmailByMemberId(memberId);

        log.info("receiverEmail = {}", receiverEmail); // 상대방
        log.info("senderEmail = {}", senderEmail);

        simpMessagingTemplate.convertAndSendToUser(receiverEmail, "/sub", messageDto); // 상대방에게
//        simpMessagingTemplate.convertAndSendToUser(userName, "/sub", messageDto);
        simpMessagingTemplate.convertAndSendToUser(senderEmail, "/sub", messageDto); // 나에게
//        return messageDto;
    }

    @ResponseBody
    @PostMapping("/chat/room")
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
        log.info("멤버아이디 : {}", principal.getUser().getId());
//        List<ChatRoomDto> chatRoomList  = chatService.findChatRoomsByMemberId(principal.getMember().getId());
//        model.addAttribute("chatRoomList", chatRoomList);
        List<ChatRoomProductDto> chatRoomProductList  = chatService.findChatRoomAndProductByMemberId(principal.getUser().getId());
        log.info("chatRoomProductList = {}", chatRoomProductList);
        chatRoomProductList.forEach((chatRoomProductDto) -> {
            if (chatRoomProductDto.getProduct().getImages().isEmpty()) {
                chatRoomProductDto.getProduct().setImages(List.of("../assets/images/index1.png"));
            }
        });
        model.addAttribute("chatRoomProductList", chatRoomProductList);
    }
}
