package com.readyauction.app.cash.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private Integer cash;


    public Boolean withdrawal(Integer cash) {
        if (this.cash >= cash) {
            this.cash -= cash;
            return true;

        }
        else return false;
    }
    public Boolean deposit(Integer cash) {
        this.cash += cash;
        return true;
    }
}