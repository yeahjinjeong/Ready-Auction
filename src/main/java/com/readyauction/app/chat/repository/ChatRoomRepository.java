package com.readyauction.app.chat.repository;

import com.readyauction.app.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        select
            c
        from
            ChatRoom c join c.chatRoomMembers m
        where
            m.memberId = :memberId
        """)
    List<ChatRoom> findChatRoomsByMemberId(@Param("memberId") Long memberId);

//    ChatRoom findChatRoomByProductId(Long productId);
}

/**
 * Hibernate:
 *     create table tbl_chat_message (
 *         chat_msg_id bigint not null auto_increment,
 *         chat_room_id bigint,
 *         created_at datetime(6),
 *         member_id bigint,
 *         message varchar(255),
 *         status smallint,
 *         primary key (chat_msg_id)
 *     ) engine=InnoDB
 * Hibernate:
 *     create table tbl_chat_room (
 *         id bigint not null auto_increment,
 *         product_id bigint,
 *         primary key (id)
 *     ) engine=InnoDB
 * Hibernate:
 *     create table tbl_chat_room_member (
 *         chat_room_id bigint not null,
 *         chat_room_member_status tinyint check (chat_room_member_status between 0 and 1),
 *         member_id bigint,
 *         chat_room_member_idx integer not null,
 *         primary key (chat_room_id, chat_room_member_idx)
 *     ) engine=InnoDB
 */