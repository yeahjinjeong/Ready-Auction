package com.readyauction.app.chat.repository;

import com.readyauction.app.chat.dto.ChatUnreadCountDto;
import com.readyauction.app.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<List<ChatMessage>> findChatMessagesByChatRoomId(Long id);

    @Query("""
        select cm
        from ChatMessage cm
        where cm.chatRoomId = :chatRoomId
        and cm.status = 0
    """)
    Optional<List<ChatMessage>> findUnreadMessagesByChatRoomId(Long chatRoomId);

    @Query("""
        select cm
        from ChatMessage cm
        where cm.chatRoomId = :chatRoomId
        and cm.status = 0
        and cm.memberId != :id
    """)
    Optional<List<ChatMessage>> findUnreadMessagesByChatRoomId(Long chatRoomId, Long id);

    // 내가 들어있는 채팅방 목록 중
    @Query("""
        select
            new com.readyauction.app.chat.dto.ChatUnreadCountDto(
            cm.chatRoomId,
            count(cm)
            )
        from
            ChatMessage cm
        where
            cm.status = 0
            and
            cm.memberId != :memberId
            and
            cm.chatRoomId = :chatRoomId
    """)
    Optional<ChatUnreadCountDto> findUnreadCountsByNotMemberId(Long memberId, Long chatRoomId);
}
