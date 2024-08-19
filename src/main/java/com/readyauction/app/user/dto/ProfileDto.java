package com.readyauction.app.user.dto;

import com.readyauction.app.user.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileDto {
    private String nickname;
    private String address;
    private Integer mannerScore;
    private String profilePicture;

    public Member toEntity() {
        return Member.builder()
                .nickname(this.nickname)
                .address(this.address)
                .mannerScore(this.mannerScore)
                .profilePicture(this.profilePicture)
                // 기타 필드 설정
                .build();
    }
}
