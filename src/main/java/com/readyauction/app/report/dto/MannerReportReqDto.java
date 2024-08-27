package com.readyauction.app.report.dto;

import com.readyauction.app.report.entity.Dislike;
import com.readyauction.app.report.entity.Like;
import com.readyauction.app.report.entity.MannerReport;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MannerReportReqDto {
    private Long memberId;
    private String isLiked;
    private List<Like> likes;
    private List<Dislike> dislikes;

    public MannerReport toMannerReportEntity(Long id) {
        boolean isLiked;
        isLiked = this.isLiked.equals("true");
        return MannerReport.builder()
                .authorId(id)
                .memberId(this.memberId)
                .isLiked(isLiked)
                .likes(this.likes)
                .dislikes(this.dislikes)
                .build();
    }

}
