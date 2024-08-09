package com.readyauction.app.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity(name = "ChatMessage")
@Table(name = "tbl_chat_message")
//@Embeddable
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_room_id", nullable = false) // 수정하기 .. 연관관계 쓰지 않고 엔티티 외래키 참조하기
    private Long chatRoomId;
    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "message")
    private String message;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column
    private Short status;
}
