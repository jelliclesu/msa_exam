package com.sparta.msa_exam.product.products;


import java.util.List;

public interface ProductRepositoryCustom {
    List<ProductResponseDto> searchProducts(ProductSearchDto searchDto);
}
