package com.readyauction.app.report.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_manner_report")
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MannerReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    @Column(name = "is_liked")
    private boolean isLiked;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tbl_manner_like",
            joinColumns = @JoinColumn(name = "manner_report_id")
    )
    @Column(name="likes")
    @Enumerated(EnumType.STRING)
    private List<Like> likes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tbl_manner_dislike",
            joinColumns = @JoinColumn(name = "manner_report_id")
    )
    @Column(name = "dislikes")
    @Enumerated(EnumType.STRING)
    private List<Dislike> dislikes;
}
