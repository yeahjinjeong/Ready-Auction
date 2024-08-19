package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopBidDto {
    Long memberId;
    Product product;
    Integer myPrice;
    Timestamp bidTime;
}
