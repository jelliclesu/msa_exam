package com.sparta.msa_exam.product.products;

import com.sparta.msa_exam.product.core.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom{
    Optional<Product> findByIdAndDeletedByIsNull(Long id);
}
