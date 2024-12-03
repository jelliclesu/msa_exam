package com.sparta.msa_exam.product.products;

import com.sparta.msa_exam.product.core.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    // 생성
    @Transactional
    @CachePut(cacheNames = "productCache", key = "#result.id")
    @CacheEvict(cacheNames = "productAllCache", allEntries = true)
    public ProductResponseDto createProduct(ProductRequestDto requestDto, String username) {
        log.info("##### Service Request :: {}, Username : {}" , requestDto, username);
        Product product = Product.createProduct(requestDto, username);
        Product savedProduct = productRepository.save(product);
        return new ProductResponseDto(savedProduct);
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findByIdAndDeletedByIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다."));
        return new ProductResponseDto(product);
    }

    // 목록 조회
    @Cacheable(cacheNames = "productAllCache", key = "methodName")
    public List<ProductResponseDto> getProducts(ProductSearchDto searchDto) {
        // 이 로그는 캐시된 데이터가 반환될 경우 출력되지 않음
        log.info("Fetching data from DB...");
        return productRepository.searchProducts(searchDto);
    }

    // 수정
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto, String username) {
        Product product = productRepository.findByIdAndDeletedByIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다."));
        product.update(requestDto, username);
        return new ProductResponseDto(product);
    }

    @Transactional
    public void deleteProduct(Long id, String username) {
        Product product = productRepository.findByIdAndDeletedByIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다."));
        product.delete(username);
    }

    @Transactional
    public void reduceProductQuantity(Long id, int quantity) {
        Product product = productRepository.findByIdAndDeletedByIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 상품이 존재하지 않습니다."));
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("수량이 부족합니다.");
        }
        product.reduceQuantity(quantity);
    }
}
