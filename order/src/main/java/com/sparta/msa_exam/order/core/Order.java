package com.sparta.msa_exam.order.core;

import com.sparta.msa_exam.order.orders.OrderRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "product_ids")
    private List<Long> productIds;

//    private LocalDateTime createdAt;
    private String createdBy;
//    private LocalDateTime updatedAt;
    private String updatedBy;
//    private LocalDateTime deletedAt;
    private String deletedBy;

    @PrePersist
    protected void onCreate() {
//        createdAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.CREATED;
        }
    }

//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }

    public static Order createOrder(List<Long> productIds, String userId) {
        return Order.builder()
                .status(OrderStatus.CREATED)
                .productIds(productIds)
                .createdBy(userId)
                .build();
    }

    public void update(OrderRequestDto requestDto, String userId) {
        this.status = OrderStatus.valueOf(requestDto.getStatus());
        this.productIds = requestDto.getProductIds();
//        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;

    }

    public void delete(String userId) {
//        this.deletedAt = LocalDateTime.now();
        this.deletedBy = userId;
    }
}
