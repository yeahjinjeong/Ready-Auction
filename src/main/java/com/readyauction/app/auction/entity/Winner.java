package com.readyauction.app.auction.entity;

import com.readyauction.app.auction.entity.PurchaseStatus;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Embeddable
public class Winner {

    private Long memberId;

    private Integer price;

    private Timestamp winnerTime;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus status; // 구매 대기, 거래중, 구매확정

    // Getters and Setters

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Timestamp getWinnerTime() {
        return winnerTime;
    }

    public void setWinnerTime(Timestamp winnerTime) {
        this.winnerTime = winnerTime;
    }

    public PurchaseStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseStatus status) {
        this.status = status;
    }
}
