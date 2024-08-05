package com.readyauction.app.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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
    @UpdateTimestamp
    private LocalDateTime createdAt;
}
