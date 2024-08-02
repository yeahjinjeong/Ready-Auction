package com.readyauction.app.auction.dto;

import com.readyauction.app.auction.entity.Category;
import com.readyauction.app.auction.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReqDto {

    private String email;

    private String name;

    private Category category;

    private String description;

    private Integer bidUnit;

    private Date endTime;

    private Date startTime;

    private Integer currentPrice;

    private Integer immediatePrice;

    private MultipartFile image;
    // Getters and Setters
}
