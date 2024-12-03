package com.sparta.msa_exam.product.products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDto implements Serializable {
    private String name;
    private String description;
    private Double minPrice;
    private Double maxPrice;
    private Integer minQuantity;
    private Integer maxQuantity;
}
