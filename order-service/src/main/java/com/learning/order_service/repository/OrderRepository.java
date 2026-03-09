package com.learning.order_service.repository;

import com.learning.order_service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(String status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status")
    Page<Order> findUserOrdersByStatus(Long userId, String status, Pageable pageable);
}

