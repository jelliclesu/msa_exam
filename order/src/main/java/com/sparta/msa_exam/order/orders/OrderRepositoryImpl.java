package com.sparta.msa_exam.order.orders;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.msa_exam.order.core.Order;
import com.sparta.msa_exam.order.core.OrderStatus;
import com.sparta.msa_exam.order.core.QOrder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderResponseDto> searchOrder(OrderSearchDto searchDto, String username) {
        List<Order> results = queryFactory
                .selectFrom(QOrder.order)
                .where(
//                        orderDateBetween(searchDto.getStartDate(), searchDto.getEndDate()),
                        statusEquals(searchDto.getStatus()),
                        isNotDeleted()
                )
                .fetch(); // 모든 결과 반환

        // 검색 결과를 DTO로 변환
        return results.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    private BooleanExpression isNotDeleted() {
        return QOrder.order.deletedBy.isNull();
    }

//    private BooleanExpression orderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
//        if (startDate != null && endDate != null) {
//            return QOrder.order.createdAt.between(startDate, endDate);
//        } else if (startDate != null) {
//            return QOrder.order.createdAt.goe(startDate);
//        } else if (endDate != null) {
//            return QOrder.order.createdAt.loe(endDate);
//        } else {
//            return null;
//        }
//    }

    private BooleanExpression statusEquals(OrderStatus status) {
        return status != null ? QOrder.order.status.eq(status) : null;
    }
}

