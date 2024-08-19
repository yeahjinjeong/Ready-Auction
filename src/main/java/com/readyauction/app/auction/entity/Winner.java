package com.readyauction.app.auction.entity;

import com.readyauction.app.auction.entity.PurchaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Winner {


    @Column(name = "winner_member_id", table = "winner") // 열 이름을 명시적으로 지정
    private Long memberId;
    @Column(table = "winner")
    private Integer price;
    @Column(table = "winner")
    private Timestamp winnerTime;

    @Column(table = "winner")
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status; // 구매 대기, 거래중, 구매확정

    @Column(table = "winner")
    @Enumerated(EnumType.STRING)
    private PurchaseCategoty category;
}
