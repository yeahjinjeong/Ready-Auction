package com.readyauction.app.dashboard.dto;

import com.readyauction.app.auction.entity.AuctionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
public class TransactionStatisticsDto {
    private Timestamp startTime;
    private Timestamp endTime;
    private AuctionStatus auctionStatus;
}
