package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.Order;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto implements Serializable {
    private Long orderId;
    private String status;
//    private LocalDateTime createdAt;
    private String createdBy;
//    private LocalDateTime updatedAt;
    private String updatedBy;
    private List<Long> productIds;
    private String message;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.status = order.getStatus().name();
//        this.createdAt = order.getCreatedAt();
        this.createdBy = order.getCreatedBy();
//        this.updatedAt = order.getUpdatedAt();
        this.updatedBy = order.getUpdatedBy();
        // Lazy Loading 해결: productIds 초기화
        this.productIds = order.getProductIds() != null ?
                new ArrayList<>(order.getProductIds()) :
                Collections.emptyList();
    }

    // 메시지만 설정하는 생성자
    public OrderResponseDto(String message) {
        this.message = message;
        this.orderId = null;
        this.status = "FAILED";
    }
}
