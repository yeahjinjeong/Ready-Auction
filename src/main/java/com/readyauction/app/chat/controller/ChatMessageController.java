package com.readyauction.app.chat.controller;

import com.readyauction.app.auction.dto.ProductDto;
import com.readyauction.app.chat.dto.ChatProductDto;
import com.readyauction.app.chat.dto.ChatProfileDto;
import com.readyauction.app.chat.dto.ChatRoomDto;
import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

//    @GetMapping("chat/profile/{productId}")
//    public ChatProductDto findProfiles_(@PathVariable Long productId
//    ) {
//        ChatProductDto chatProductDto = chatService.findProductById(productId);
//        log.debug(chatProductDto.toString());
//        return chatProductDto;
//    }

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
}
