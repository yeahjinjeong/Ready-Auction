package com.readyauction.app.chat.repository;

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
}
