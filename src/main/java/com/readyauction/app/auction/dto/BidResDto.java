package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.BidStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidResDto {
    BidStatus bidStatus;
    Integer bidCurrentPrice;
    Timestamp bidSuccessTime;
}
