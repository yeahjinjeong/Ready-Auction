package com.readyauction.app.cash.entity;

import com.readyauction.app.auction.entity.Product;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne
    private Account senderAccount;

    @ManyToOne
    private Account receiverAccount;

    private Integer payAmount;

    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Getters and Setters
}
