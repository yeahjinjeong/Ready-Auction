package com.readyauction.app.admin.dashboard.dto;

import com.readyauction.app.auction.entity.AuctionStatus;
import com.readyauction.app.auction.entity.PurchaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuctionBidDto {
    private Long productId;
    private String productName;
    private Long sellerId;
    private Integer currentPrice;
    private AuctionStatus auctionStatus;
    private Long winnerOrBidderMemberId; // 경매 상태에 따라 입찰자 또는 낙찰자 ID
    private Integer bidOrWinnerPrice; // 경매 상태에 따라 입찰 금액 또는 낙찰 금액
    private PurchaseStatus purchaseStatus; // END 상태일 경우에만 필요
}
