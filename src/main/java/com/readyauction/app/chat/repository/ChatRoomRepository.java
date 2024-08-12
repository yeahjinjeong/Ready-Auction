package com.readyauction.app.chat.repository;

import com.readyauction.app.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        select
            c
        from
            ChatRoom c join c.chatRoomMembers m
        where
            m.memberId = :memberId
        """)
    Optional<List<ChatRoom>> findChatRoomsByMemberId(@Param("memberId") Long memberId);
    Optional<ChatRoom> findChatRoomByProductId(Long productId);

    Optional<Long> findChatRoomIdByProductId(Long productId);

//    @Query("""
//        select
//            cm.memberNickname
//        from
//            ChatRoom c join c.chatRoomMembers cm
//        where
//            cm.memberId != :memberId
//        """)
//    Optional<String> findMemberNickNameByNotMemberId(@Param("memberId") Long memberId);
}

/**
 * Hibernate:
 *     create table tbl_chat_message (
 *         id bigint not null auto_increment,
 *         chat_room_id bigint not null,
 *         created_at datetime(6),
 *         member_id bigint,
 *         message varchar(255),
 *         status smallint,
 *         primary key (id)
 *     ) engine=InnoDB
 * Hibernate:
 *     create table tbl_chat_room (
 *         id bigint not null auto_increment,
 *         created_at datetime(6),
 *         last_message varchar(255),
 *         last_message_updated_at datetime(6),
 *         product_id bigint,
 *         primary key (id)
 *     ) engine=InnoDB
 * Hibernate:
 *     create table tbl_chat_room_member (
 *         chat_room_id bigint not null,
 *         chat_room_member_status tinyint not null check (chat_room_member_status between 0 and 1),
 *         member_id bigint not null,
 *         member_nickname varchar(255) not null,
 *         chat_room_member_idx integer not null,
 *         primary key (chat_room_id, chat_room_member_idx)
 *     ) engine=InnoDB
 * Hibernate:
 *     alter table tbl_chat_room_member
 *        add constraint FKcsvyu7g5e8xhpkgfk34teulba
 *        foreign key (chat_room_id)
 *        references tbl_chat_room (id)
 */