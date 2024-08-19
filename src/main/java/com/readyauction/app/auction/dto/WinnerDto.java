package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WinnerDto {
    Long userId;
    Product product;
    WinnerReqDto winnerReqDto;
}
