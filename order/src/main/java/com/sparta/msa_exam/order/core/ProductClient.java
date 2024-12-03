package com.sparta.msa_exam.order.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ProductResponseDto getProductById(@PathVariable Long id);

    @GetMapping("/products/{id}/reduceQuantity")
    void reduceProductQuantity(@PathVariable Long id, @RequestParam int quantity);

}
