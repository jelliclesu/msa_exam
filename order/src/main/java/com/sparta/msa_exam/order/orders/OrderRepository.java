package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.core.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom{
    Optional<Order> findByIdAndDeletedByIsNull(Long id);
}
