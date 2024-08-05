package com.readyauction.app.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {
//    private final ChatService chatService;

//    @MessageMapping("foo") // pub/foo
//    @SendTo("/sub/foo") //
//    public MessageDto chatting(MessageDto messageDto) {
//        log.debug("message = {}", messageDto);
//        chatService.save(messageDto);
//        return messageDto;
//    }

    @GetMapping("chat/list")
    public void chatList(
//            @PathVariable Long memberId,
            Model model) {
//        List<ChatRoomDto> chatRoomList  = chatService.findChatRoomsByMemberId(0L);
//        log.debug(chatRoomList.toString());
//        model.addAttribute("chatRoomList", chatRoomList);
    }
}
