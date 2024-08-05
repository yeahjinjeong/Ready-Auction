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
    @Column(name = "member_id")
    private Long memberId;
    @Column(name = "chat_room_member_status")
    @Enumerated(EnumType.ORDINAL)
    private ChatRoomMemberStatus chatRoomMemberStatus;
}
