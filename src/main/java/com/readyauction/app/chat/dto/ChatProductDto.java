package com.readyauction.app.chat.dto;

import com.readyauction.app.auction.entity.Category;
import com.readyauction.app.auction.entity.Product;
import com.readyauction.app.auction.entity.PurchaseStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatProductDto {
    private Long productId;
    private Long sellerId;
    private Long winnerId;
    private String productName;
    private Category category;
    private String image;
    private Integer price; // 낙찰가
    private PurchaseStatus status; // 구매 대기, 거래중, 구매확정

    public static ChatProductDto toChatProductDto(Product product) {
        return new ChatProductDto(
                product.getId(),
                product.getMemberId(), // sellerId
                product.getWinner().getMemberId(), // winnerId
                product.getName(), // productName
                product.getCategory(),
                (product.getImages() != null && !product.getImages().isEmpty()) ? product.getImages().get(0) : null,
                product.getWinner().getPrice(),
                product.getWinner().getStatus()
        );
    }
}
