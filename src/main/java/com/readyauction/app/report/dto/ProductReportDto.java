package com.readyauction.app.report.dto;

import com.readyauction.app.report.entity.ProductReport;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Setter
@Getter
public class ProductReportDto {
    private String reportReason; // 신고 유형
    private Long productId; // 상품 ID
    private Long userId;

    public ProductReport toEntity() {
        return ProductReport.builder()
                .userId(this.userId)
                .productId(this.productId)
                .reportReason(this.reportReason)
                .build();
    }
}