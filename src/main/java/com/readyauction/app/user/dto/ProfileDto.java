package com.readyauction.app.user.dto;

import com.readyauction.app.report.entity.Dislike;
import com.readyauction.app.report.entity.Like;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProfileDto {
    private String nickname;
    private String address;
    private Integer mannerScore;
    private String profilePicture;
    private Map<Like, Long> likeCounts;
    private Map<Dislike, Long> dislikeCounts;
}
