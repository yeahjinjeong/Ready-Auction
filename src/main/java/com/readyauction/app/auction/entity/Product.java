package com.readyauction.app.auction.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SecondaryTable(
        name = "winner",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "product_id",referencedColumnName = "id")
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

    private Integer startPrice;

    private Integer currentPrice;

    private Integer immediatePrice;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images;

    @Enumerated(EnumType.STRING)
    private AuctionStatus auctionStatus;
    // Winner를 임베디드 객체로 선언
    @Embedded
    private Winner winner;

    public boolean hasWinner() {
        return winner != null;
    }
}
