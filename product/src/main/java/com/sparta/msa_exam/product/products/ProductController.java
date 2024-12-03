package com.sparta.msa_exam.product.products;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    @Value("${server.port}")
    private String serverPort;
    private final ProductService productService;

//    @PostMapping("/test")
//    public ResponseEntity<String> logHeadersAndRespond(
//            @RequestHeader HttpHeaders headers,
//            @RequestBody ProductRequestDto requestDto) {
//        log.info("Received Headers: {}", headers);
//        log.info("Received Request Body: {}", requestDto);
//
//        // 응답만 테스트할 경우 간단히 문자열 반환
//        return ResponseEntity.ok("Headers logged successfully");
//    }

    // 생성
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto,
                                                            @RequestHeader(value = "X-Username") String username) {
        log.info("##### Controller Request :: {}, Username : {}" , requestDto, username);
        ProductResponseDto product = productService.createProduct(requestDto, username);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(product);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(product);
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getProducts(ProductSearchDto searchDto) {
        log.info("Search DTO: {}", searchDto);
        List<ProductResponseDto> product = productService.getProducts(searchDto);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(product);
    }


    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                            @RequestBody ProductRequestDto requestDto,
                                            @RequestHeader(value = "X-Username", required = true) String username) {
        ProductResponseDto product = productService.updateProduct(id, requestDto, username);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(product);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?>  deleteProduct(@PathVariable Long id,
                               @RequestHeader(value = "X-Username", required = true) String username) {
        productService.deleteProduct(id, username);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort).build();
    }

    // 수량 감소
    @GetMapping("/{id}/reduceQuantity")
    public ResponseEntity<?> reduceProductQuantity(@PathVariable Long id, @RequestParam int quantity) {
        productService.reduceProductQuantity(id, quantity);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort).build();
    }
}
