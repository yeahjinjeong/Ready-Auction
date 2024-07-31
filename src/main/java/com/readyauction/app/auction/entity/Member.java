package com.readyauction.app.auction.entity;

import jakarta.persistence.*;

@Entity
public class Member extends User{
    private String nickname;
    private String picture;
    private String address;
    private Integer mannersScore;
    private Integer cashPoint;
    // Getters and Setters
}