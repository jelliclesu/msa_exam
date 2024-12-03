package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.OrderStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderSearchDto implements Serializable {
    private OrderStatus status;
    private List<Long> productIds;
    private String sortBy;
}