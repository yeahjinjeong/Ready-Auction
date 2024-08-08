package com.readyauction.app.chat.entity;

import com.readyauction.app.chat.dto.MessageDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity(name = "ChatRoom")
@Table(name = "tbl_chat_room")
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id")
    private Long productId;
    // 채팅방을 생성할 때 productId로 판매자와 낙찰자를 조회
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tbl_chat_room_member",
            joinColumns = @JoinColumn(name = "chat_room_id")
    )
    @OrderColumn(name = "chat_room_member_idx")
    private List<ChatRoomMember> chatRoomMembers;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(
//            name = "tbl_chat_message",
//            joinColumns = @JoinColumn(name = "chat_room_id")
//    )
//    @OrderColumn(name = "chat_message_idx")
//    private List<ChatMessage> chatMessages;

    @CreationTimestamp
    private LocalDateTime createdAt;
//
//    public void changeLastMessage(MessageDto messageDto) {
//        this.lastMessage = messageDto.getMessage();
//        this.lastMessageUpdatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(messageDto.getCreatedAt()), ZoneId.systemDefault());
//    }
}
