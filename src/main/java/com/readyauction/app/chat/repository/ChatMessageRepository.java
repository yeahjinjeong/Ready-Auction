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

    // 내가 들어있는 채팅방 목록 중
    @Query("""
        select count(cm)
        from
            ChatMessage cm
        where
            cm.status = 0
            and
            cm.memberId != :memberId
            and
            cm.chatRoomId =
            (select
                c.id
            from
                ChatRoom c join c.chatRoomMembers m
            where
                m.memberId = :memberId
            order by
                c.createdAt desc)
    """)
    Optional<List<Integer>> findUnreadCountsByNotMemberId(Long id);
}
