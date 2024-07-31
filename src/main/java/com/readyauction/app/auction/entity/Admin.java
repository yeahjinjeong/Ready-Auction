package com.readyauction.app.auction.entity;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User {
    private String nickname;
    private String picture;

    // Getters and Setters
}