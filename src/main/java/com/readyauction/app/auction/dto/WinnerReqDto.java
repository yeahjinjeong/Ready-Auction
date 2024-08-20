package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.PurchaseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WinnerReqDto {

   private Long productId;
   private Integer buyPrice;
   private Timestamp buyTime;
   private PurchaseCategory category;

    @Override
    public String toString() {
        return "WinnerReqDto [productId=" + productId + ", buyPrice=" + buyPrice + ", buyTime=" + buyTime;
    };
}
