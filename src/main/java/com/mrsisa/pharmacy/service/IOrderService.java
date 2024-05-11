package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IOrderService extends IJPAService<Order> {

    Page<Order> getOrders(Pageable pageable);

    Offer createOffer(Long orderId, Long supplierId, LocalDateTime deliveryDate, Double totalCost, boolean multipleOffersForOrder);

    Order getOrder(Long id);

    Page<Order> getOrderForPharmacy(Pharmacy pharmacy, OrderStatus orderStatus, Pageable pageable);

    void acceptOffer(Long orderId, Long offerId);

    Order addOrderItem(Long orderId, Long medicineId, Integer quantity, Boolean isNew, Double newPrice);

    Order removeOrderItem(Long orderId, Long itemId);

    Page<MedicineOrderInfo> getOrderItems(Pharmacy pharmacy, Long orderId, String name, Pageable pageable);

    Order publish(Long orderId);

    Order updateOrder(Long orderId, LocalDateTime dueDate);

    Order updateOrderItem(Long orderId, Long itemId, Integer quantity);

    Long getOfferCount(Order order);
}
