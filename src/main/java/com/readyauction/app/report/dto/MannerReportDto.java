package com.readyauction.app.report.dto;

import com.readyauction.app.report.entity.Dislike;
import com.readyauction.app.report.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MannerReportDto {
    private Long memberId;
    private boolean isLiked;
    private boolean isDisliked;
    private List<Like> likes;
    private List<Dislike> dislikes;
}
