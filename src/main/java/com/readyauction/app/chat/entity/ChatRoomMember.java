package com.readyauction.app.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMember {
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    @Column(name = "member_nickname", nullable = false)
    private String memberNickname;
    @Column(name = "chat_room_member_status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ChatRoomMemberStatus chatRoomMemberStatus;
}
