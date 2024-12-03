package com.sparta.msa_exam.product.products;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_exam.product.core.Product;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.msa_exam.product.core.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductResponseDto> searchProducts(ProductSearchDto searchDto) {
        // QueryDSL 로 동적 검색 수행
        List<Product> results = queryFactory
                .selectFrom(product)
                .where(
                        nameContains(searchDto.getName()),              // 이름 검색 조건
                        descriptionContains(searchDto.getDescription()), // 설명 검색 조건
                        priceBetween(searchDto.getMinPrice(), searchDto.getMaxPrice()), // 가격 범위 조건
                        quantityBetween(searchDto.getMinQuantity(), searchDto.getMaxQuantity()), // 수량 범위 조건
                        isNotDeleted()
                )
                .fetch();                          // 모든 결과 가져오기

        // 검색 결과를 DTO로 변환
        return results.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    // 삭제되지 않은 데이터 조건 (deletedBy: null)
    private BooleanExpression isNotDeleted() {
        return product.deletedBy.isNull();
    }

    // 이름 포함 조건
    private BooleanExpression nameContains(String name) {
        return name != null ? product.name.containsIgnoreCase(name) : null;
    }

    // 설명 포함 조건
    private BooleanExpression descriptionContains(String description) {
        return description != null ? product.description.containsIgnoreCase(description) : null;
    }

    // 가격 범위 조건
    private BooleanExpression priceBetween(Double minPrice, Double maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return product.supplyPrice.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return product.supplyPrice.goe(minPrice);
        } else if (maxPrice != null) {
            return product.supplyPrice.loe(maxPrice);
        } else {
            return null;
        }
    }

    // 수량 범위 조건
    private BooleanExpression quantityBetween(Integer minQuantity, Integer maxQuantity) {
        if (minQuantity != null && maxQuantity != null) {
            return product.quantity.between(minQuantity, maxQuantity);
        } else if (minQuantity != null) {
            return product.quantity.goe(minQuantity);
        } else if (maxQuantity != null) {
            return product.quantity.loe(maxQuantity);
        } else {
            return null;
        }
    }
}
