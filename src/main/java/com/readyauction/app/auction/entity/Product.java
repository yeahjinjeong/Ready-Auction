package com.readyauction.app.auction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "tbl_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SecondaryTable(
        name = "tbl_winner",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "product_id",referencedColumnName = "id")
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Long memberId;

    @NotEmpty(message = "상품 이름은 필수 항목입니다.")
    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String description;
    @NotNull(message = "입찰 단가는 필 수 입니다.")
    private Integer bidUnit;

    @NotNull(message = "종료 시간은 필수 항목입니다.")
    private Timestamp endTime;

    @NotNull(message = "시작 시간은 필수 항목입니다.")
    private Timestamp startTime;

    private Integer startPrice;

    private Integer currentPrice;

    private Integer immediatePrice;

//    @NotEmpty(message = "최소 하나의 이미지는 필수 항목입니다.")
    @ElementCollection
    @CollectionTable(name = "tbl_product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", nullable = true)
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
