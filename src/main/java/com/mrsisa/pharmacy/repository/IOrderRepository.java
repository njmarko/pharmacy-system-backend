package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "select o from Order o  where o.dueDate > :currentDate and o.active = true and o.orderStatus = :status",
            countQuery = "select count(o) from Order o where o.dueDate > :currentDate and o.active=true and o.orderStatus = :status")
    Page<Order> getOrders(@Param("currentDate") LocalDateTime currentDate,
                          @Param("status") OrderStatus status,
                          Pageable pageable);

    Optional<Order> getOrderByActiveTrueAndDueDateAfterAndId(LocalDateTime time, Long id);


    @Query("select o from Order o where o.id = :id and o.active=true")
    Optional<Order> getOrderDetails(@Param("id") Long id);

    @Query(value = "select o from Order o where o.pharmacy.id=:id and (:status is null or o.orderStatus=:status) and o.active=true",
            countQuery = "select count(o) from Order o where o.pharmacy.id=:id and (:status is null or o.orderStatus=:status) and o.active=true")
    Page<Order> findOrdersForPharmacy(@Param("id") Long pharmacyId,
                                      @Param("status") OrderStatus orderStatus,
                                      Pageable pageable);
}
