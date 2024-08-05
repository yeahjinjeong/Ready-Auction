package com.readyauction.app.cash.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class Charge{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private Integer chargeAmount;

    private Timestamp date;

    @Enumerated(EnumType.STRING)
    private ChargeStatus status;

    // Getters and Setters
}
