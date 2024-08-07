package com.readyauction.app.chat.dto;

import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.user.entity.Gender;
import com.readyauction.app.user.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatProfileDto {
    private Long sellerId;
    private String sellerNickName;
    private String sellerProfilePicture;
    private Integer sellerMannerScore;

    private Long winnerId;
    private String winnerNickName;
    private String winnerProfilePicture;
    private Integer winnerMannerScore;

    public static ChatProfileDto toChatProfileDto(Member seller, Member winner) {
        return new ChatProfileDto(
                seller.getId(),
                seller.getNickname(),
                seller.getProfilePicture(),
                seller.getMannerScore(),
                winner.getId(),
                winner.getNickname(),
                winner.getProfilePicture(),
                winner.getMannerScore()
        );
    }
}
