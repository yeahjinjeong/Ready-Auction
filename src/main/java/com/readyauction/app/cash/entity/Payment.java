package com.readyauction.app.cash.entity;

import com.readyauction.app.auction.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private Long productId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Getters and Setters
}
