package com.sparta.msa_exam.order.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    @Value("${server.port}")
    private String  serverPort;

    private final OrderService orderService;

    // 생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderRequestDto requestDto,
            @RequestHeader("X-Username") String username,
            @RequestParam(defaultValue = "false") boolean fail) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "권한이 없습니다.");
        }
        OrderResponseDto order = orderService.createOrder(requestDto, username, fail);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(order);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(order);
    }

    // 목록 조회
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(OrderSearchDto searchDto,
                                            @RequestHeader("X-Username") String username) {
        List<OrderResponseDto> order = orderService.getOrders(searchDto, username);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(order);
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id,
                                        @RequestBody OrderRequestDto requestDto,
                                        @RequestHeader("X-Username") String username) {
        OrderResponseDto order = orderService.updateOrder(id, requestDto, username);
        return ResponseEntity.ok()
                .header("Server-Port", serverPort)
                .body(order);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id,
                            @RequestHeader("X-Username") String username) {
        orderService.deleteOrder(id, username);
        return ResponseEntity.ok().header("Server-Port", serverPort).build();
    }
}
