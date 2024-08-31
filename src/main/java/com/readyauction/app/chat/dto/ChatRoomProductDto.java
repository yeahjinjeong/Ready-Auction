package com.readyauction.app.chat.dto;

import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.Category;
import com.readyauction.app.auction.entity.Images;
import com.readyauction.app.auction.entity.PurchaseStatus;
import com.readyauction.app.chat.entity.ChatRoomMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomProductDto {
    private Long id;
    private Long productId;
    private List<ChatRoomMember> chatRoomMembers;
    private String lastMessage;
    private LocalDateTime lastMessageUpdatedAt;
    private LocalDateTime createdAt;
    private String productName;
    private List<String> images;
    private PurchaseStatus status; // 구매 대기, 거래중, 구매확정
}
