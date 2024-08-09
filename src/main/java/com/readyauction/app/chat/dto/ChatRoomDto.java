package com.readyauction.app.chat.dto;

import com.readyauction.app.chat.entity.ChatRoom;
import com.readyauction.app.chat.entity.ChatRoomMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private Long productId;
    private List<ChatRoomMember> chatRoomMembers;
//    private String anotherMemberNickname;
    private String lastMessage;
    private LocalDateTime lastMessageUpdatedAt;
    private LocalDateTime createdAt;

    public static ChatRoomDto toChatRoomDto(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getProductId(),
                chatRoom.getChatRoomMembers(),
                chatRoom.getLastMessage(),
                chatRoom.getLastMessageUpdatedAt(),
                chatRoom.getCreatedAt()
        );
    }
}
