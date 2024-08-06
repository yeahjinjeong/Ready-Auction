package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;

    private String name;

    private Category category;

//    private String description;

    private Integer bidUnit;

    private Timestamp endTime;

//    private Timestamp startTime;

    private Integer currentPrice;

    private Integer immediatePrice;

    private String imgUrl;

    @Override
    public String toString() {
        return "ProductDto [id=" + id + ", name=" + name + ", category=" + category + ", bidUnit=" + bidUnit + ", endTime=" + endTime + ", imgUrl=" + imgUrl + "]";
    }
}
