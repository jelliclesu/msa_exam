package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.Order;
import com.sparta.msa_exam.order.core.ProductClient;
import com.sparta.msa_exam.order.core.ProductResponseDto;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @PostConstruct
    public void registerEventListener() {
        circuitBreakerRegistry.circuitBreaker("orderService").getEventPublisher()
                .onStateTransition(event -> log.info("#######CircuitBreaker State Transition: {}", event)) // 상태 전환 이벤트 리스너
                .onFailureRateExceeded(event -> log.info("#######CircuitBreaker Failure Rate Exceeded: {}", event)) // 실패율 초과 이벤트 리스너
                .onCallNotPermitted(event -> log.info("#######CircuitBreaker Call Not Permitted: {}", event)) // 호출 차단 이벤트 리스너
                .onError(event -> log.info("#######CircuitBreaker Error: {}", event)); // 오류 발생 이벤트 리스너
    }

    // 생성
    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "fallbackGetProducts")
    public OrderResponseDto createOrder(OrderRequestDto requestDto, String username, boolean fail) {
        // fail 파라미터가 true 일 경우 예외 발생
        if (fail) {
            throw new RuntimeException("Simulated Product API failure");
        }

        for (Long productId : requestDto.getProductIds()) {
            try {
                ProductResponseDto product = productClient.getProductById(productId);
                log.info("Product Retrieved: {}", product);

                if (product.getQuantity() < 1) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + productId + " is out of stock.");
                }
            } catch (FeignException.NotFound ex) {
                log.error("Product Not Found: {}", productId, ex);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + productId + " is not found.");
            }
        }

        for (Long productId : requestDto.getProductIds()) {
            productClient.reduceProductQuantity(productId, 1);
        }

        Order order = Order.createOrder(requestDto.getProductIds(), username);
        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder);
    }

    public OrderResponseDto fallbackGetProducts(OrderRequestDto requestDto, String username, boolean fail, Throwable t) {
        log.error("Fallback Triggered: Unable to call Product Service for User: {}", username);
        log.error("Error Details: ", t);

        return new OrderResponseDto("잠시 후에 주문 추가를 요청 해주세요.");
    }

    // 단건 조회
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orderCache", key = "#id")
    public OrderResponseDto getOrderById(Long id) {
        log.info("Fetching order from DB with ID: {}", id); // 캐시된 경우 출력되지 않음
        Order order = orderRepository.findByIdAndDeletedByIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."));

        return new OrderResponseDto(order);
    }

    // 목록 조회
    public List<OrderResponseDto> getOrders(OrderSearchDto searchDto, String username) {
        return orderRepository.searchOrder(searchDto, username);
    }

    // 수정
    @Transactional
    public OrderResponseDto updateOrder(Long id, OrderRequestDto requestDto, String username) {
        Order order = orderRepository.findByIdAndDeletedByIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."));
        order.update(requestDto, username);

        return new OrderResponseDto(order);
    }

    // 삭제
    @Transactional
    public void deleteOrder(Long id, String username) {
        Order order = orderRepository.findByIdAndDeletedByIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."));
        order.delete(username);
    }
}
