package com.readyauction.app.chat.entity;

import com.readyauction.app.chat.dto.ChatRoomDto;
import com.readyauction.app.chat.dto.MessageDto;
import com.readyauction.app.chat.repository.ChatMessageRepository;
import com.readyauction.app.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChatTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("ddl-auto=create 확인")
    void test() {
    }

    @Test
    @DisplayName("[채팅-삽입] 채팅방 생성")
    void insertChatRoom() {
        ChatRoom chatRoom = ChatRoom.builder()
                .productId(1L)
                .chatRoomMembers(List.of(new ChatRoomMember(2L, ChatRoomMemberStatus.Seller), new ChatRoomMember(3L, ChatRoomMemberStatus.Winner)))
                .build();
        ChatRoom chatRoomResult = chatRoomRepository.save(chatRoom);
        assertThat(chatRoomResult.getChatRoomMembers()).isNotEmpty();
        System.out.println(chatRoomResult.getChatRoomMembers());
        // [ChatRoomMember(memberId=2, chatRoomMemberStatus=Seller), ChatRoomMember(memberId=3, chatRoomMemberStatus=Winner)]
    }

    @Test
    @DisplayName("[채팅-조회] 채팅방 조회")
    void findChatRoomsByMemberId() {
        List<ChatRoom> chatRoomList = chatRoomRepository.findChatRoomsByMemberId(3L);
        System.out.println(chatRoomList);
        chatRoomList.stream().map(ChatRoomDto::toChatRoomDto).toList();
        System.out.println(chatRoomList);
    }

    @Test
    @DisplayName("[채팅-조회] 채팅방 id 조회")
    void findChatRoomByProductId() {
        // given
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByProductId(1L);
        // when
        ChatRoomDto chatRoomDto = ChatRoomDto.toChatRoomDto(chatRoom);
        System.out.println(chatRoomDto);
        // then
    }

    @Test
    @DisplayName("[채팅-조회] 채팅 메시지 조회")
    void findChatMessagesByChatRoomId() {
        // given
        List<ChatMessage> chatMessageList = chatMessageRepository.findChatMessagesByChatRoomId(1L);
        // when
        List<MessageDto> messageDtos = chatMessageList.stream().map(MessageDto::toChatMessageDto).toList();
        // then
        System.out.println(messageDtos);
    }
}
