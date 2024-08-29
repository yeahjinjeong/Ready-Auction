package com.readyauction.app.cash.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "tbl_payment")
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

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentCategory category;
    // Getters and Setters
}
