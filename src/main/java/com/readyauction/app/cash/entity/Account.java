package com.readyauction.app.cash.entity;

import jakarta.persistence.*;
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Integer cash;
}