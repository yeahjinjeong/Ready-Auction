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
public class ProductRepDto {
    private Long id;
    private String name;
    private Category category;
    private String description;
    private Integer bidUnit;
    private Timestamp endTime;
    private Timestamp startTime;
    private Integer startPrice;
    private Integer currentPrice;
    private Integer immediatePrice;
    private List<String> imgUrl;
    private Long memberId;  // 판매자 ID 추가
    private String nickName;

    @Override
    public String toString() {
        return "ProductReqDto [name=" + name + ", category=" + category + ", description=" + description + ", bidUnit=" + bidUnit + ", endTime=" + endTime + ", startTime=" + startTime + ", imgUrl=" + imgUrl + "]";
    }
}