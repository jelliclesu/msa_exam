package com.sparta.msa_exam.product.core;

import com.sparta.msa_exam.product.products.ProductRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "products")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer supplyPrice;
    private Integer quantity;

//    private LocalDateTime createdAt;
    private String createdBy;
//    private LocalDateTime updatedAt;
    private String updatedBy;
//    private LocalDateTime deletedAt;
    private String deletedBy;

    public static Product createProduct(ProductRequestDto requestDto, String username) {
        return Product.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .supplyPrice(requestDto.getSupplyPrice())
                .quantity(requestDto.getQuantity())
                .createdBy(username)
                .build();
    }

//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }

    public void update(ProductRequestDto requestDto, String username) {
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.supplyPrice = requestDto.getSupplyPrice();
        this.quantity = requestDto.getQuantity();
//        this.updatedAt = LocalDateTime.now();
        this.updatedBy = username;
    }

    public void delete(String username) {
//        this.deletedAt = LocalDateTime.now();
        this.deletedBy = username;
    }

    public void reduceQuantity(int i) {
        this.quantity = quantity - i;
    }
}
