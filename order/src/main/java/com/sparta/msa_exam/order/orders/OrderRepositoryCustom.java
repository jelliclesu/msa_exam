package com.sparta.msa_exam.order.orders;

import java.util.List;

public interface OrderRepositoryCustom {
    List<OrderResponseDto> searchOrder(OrderSearchDto searchDto, String userId);
}
