package com.readyauction.app.chat.controller;

import com.readyauction.app.chat.dto.ChatRoomDto;
import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatService chatService;

    @GetMapping("chat/{productId}")
    public List<MessageDto> findMessages(
            @PathVariable Long productId,
            Model model
    ) {
        // productId로 chatRoomId를 찾아서 chatRoomId로 메시지들을 찾고
        ChatRoomDto chatRoomDto = chatService.findChatRoomByProductId(productId);
        List<MessageDto> messageDtos = chatService.findChatMessagesByChatRoomId(chatRoomDto.getId());
        log.debug(messageDtos.toString());
        // 그 중 내가 보낸 메시지들만 오른쪽으로 정렬하고
        // 내가 보내지 않은 메시지들은 왼쪽으로 정렬한다.
        return messageDtos;
    }
}
