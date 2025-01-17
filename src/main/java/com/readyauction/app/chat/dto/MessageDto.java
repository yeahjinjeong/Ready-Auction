package com.readyauction.app.chat.dto;

import com.readyauction.app.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String message;
    private Long createdAt;
    private LocalDateTime createdAt2;
    private Short status;
    private Long receiverId;

    public ChatMessage toChatMessageEntity() {
        return ChatMessage.builder()
                .id(createdAt)
                .chatRoomId(this.chatRoomId)
                .memberId(this.senderId)
                .message(this.message)
                .createdAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(this.createdAt), ZoneId.systemDefault()))
                .status(this.status)
                .build();
    }

    public static MessageDto toChatMessageDto(ChatMessage chatRoomMessage) {
        return new MessageDto(
                chatRoomMessage.getId(),
                chatRoomMessage.getChatRoomId(),
                chatRoomMessage.getMemberId(),
                chatRoomMessage.getMessage(),
                null,
                chatRoomMessage.getCreatedAt(),
                chatRoomMessage.getStatus(),
                null
        );
    }
}
