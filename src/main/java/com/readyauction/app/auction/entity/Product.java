package com.readyauction.app.auction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SecondaryTable(
        name = "winner",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "product_id")
)
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

    @Enumerated(EnumType.STRING)
    private AuctionStatus auctionStatus;
    // Winner를 임베디드 객체로 선언
    @Embedded
    private Winner winner;
}
