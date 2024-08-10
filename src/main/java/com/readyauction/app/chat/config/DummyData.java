package com.readyauction.app.chat.config;

import com.readyauction.app.chat.entity.ChatRoom;
import com.readyauction.app.chat.entity.ChatRoomMember;
import com.readyauction.app.chat.entity.ChatRoomMemberStatus;
import com.readyauction.app.chat.repository.ChatMessageRepository;
import com.readyauction.app.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
public class DummyData {
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ChatMessageRepository chatMessageRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        // 더미 데이터 저장 로직
        ChatRoom chatRoom = ChatRoom.builder()
                .id(1L)
                .chatRoomMembers(List.of
                        (new ChatRoomMember(0L, "Yejin", ChatRoomMemberStatus.Seller),
                        new ChatRoomMember(3L, "Jiyoung", ChatRoomMemberStatus.Winner)
                        ))
                .productId(1L)
                .build();
        chatRoomRepository.save(chatRoom);
    }
}
