package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReqDto {

    private String name;

    private Category category;

    private String description;

    private Integer bidUnit;

    private Timestamp endTime;

    private Timestamp startTime;

    private Integer currentPrice;

    private Integer immediatePrice;

    private String imgUrl;

    @Override
    public String toString() {
        return "ProductReqDto [name=" + name + ", category=" + category + ", description=" + description + ", bidUnit=" + bidUnit + ", endTime=" + endTime + ", startTime=" + startTime + ", imgUrl=" + imgUrl + "]";
    };
}
