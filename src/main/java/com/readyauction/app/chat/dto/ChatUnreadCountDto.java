package com.readyauction.app.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatUnreadCountDto {
    private Long chatRoomId;
    private Long count;
}
