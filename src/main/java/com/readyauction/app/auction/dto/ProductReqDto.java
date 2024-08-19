package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReqDto {
    private String name;
    private Category category;
    private String description;
    private Integer bidUnit;
    private Timestamp endTime;
    private Timestamp startTime;
    private Integer currentPrice;
    private Integer immediatePrice;
    private List<String> imgUrls;
    private Long memberId;  // 판매자 ID 추가

    @Override
    public String toString() {
        return "ProductReqDto [name=" + name + ", category=" + category + ", description=" + description + ", bidUnit=" + bidUnit + ", endTime=" + endTime + ", startTime=" + startTime + "]";
    }
}