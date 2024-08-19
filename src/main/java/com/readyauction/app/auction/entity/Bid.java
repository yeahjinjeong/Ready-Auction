package com.readyauction.app.auction.entity;

import groovy.lang.Lazy;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @Lazy
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer myPrice;

    private Timestamp bidTime;

    @Enumerated(EnumType.STRING)
    private BidStatus bidStatus;



    // Getters and Setters
}





