package com.readyauction.app.auction.entity;

// Getters and Setters

import jakarta.persistence.*;

import java.sql.Date;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;

    private Integer bidUnit;

    private Date endTime;

    private Date startTime;

    private Integer currentPrice;

    private Integer immediatePrice;

    private String image;
    // Getters and Setters
}
