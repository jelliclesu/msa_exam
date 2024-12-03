package com.sparta.msa_exam.product.products;

import com.sparta.msa_exam.product.core.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Integer supplyPrice;
    private Integer quantity;
//    private LocalDateTime createdAt;
    private String createdBy;
//    private LocalDateTime updatedAt;
    private String updatedBy;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.supplyPrice = product.getSupplyPrice();
        this.quantity = product.getQuantity();
//        this.createdAt = product.getCreatedAt();
        this.createdBy = product.getCreatedBy();
//        this.updatedAt = product.getUpdatedAt();
        this.updatedBy = product.getUpdatedBy();
    }
}
