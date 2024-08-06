package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidDto {

   private Long productId;
   private Integer bidPrice;
   private Timestamp bidTime;


    @Override
    public String toString() {
        return "BidDto [productId=" + productId + ",     bidPrice=" + bidPrice + ", bidTime=" + bidTime + "]";
    };
}
