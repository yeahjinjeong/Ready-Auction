package com.readyauction.app.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WinnerReqDto {

   private Long productId;
   private Integer buyPrice;
   private Timestamp buyTime;


    @Override
    public String toString() {
        return "WinnerReqDto [productId=" + productId + ", buyPrice=" + buyPrice + ", buyTime=" + buyTime;
    };
}
