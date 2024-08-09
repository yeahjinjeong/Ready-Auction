package com.readyauction.app.chat.repository;

import com.readyauction.app.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<List<ChatMessage>> findChatMessagesByChatRoomId(Long id);
}
