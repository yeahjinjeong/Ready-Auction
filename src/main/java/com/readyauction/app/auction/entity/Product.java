package com.readyauction.app.auction.entity;

// Getters and Setters

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    private Integer bidUnit;

    private Timestamp endTime;

    private Timestamp startTime;

    private Integer currentPrice;

    private Integer immediatePrice;

    private String image;
    // Getters and Setters
}
