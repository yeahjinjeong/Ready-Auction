package com.readyauction.app.auction.entity;

import com.readyauction.app.member.entity.Member;
import jakarta.persistence.*;
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Long cash;
}