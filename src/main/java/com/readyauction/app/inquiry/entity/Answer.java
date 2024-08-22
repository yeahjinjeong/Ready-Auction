package com.readyauction.app.inquiry.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Column(name = "answer_author_id")
    private Long authorId;
    private String content;
    private Timestamp answeredAt;
}
