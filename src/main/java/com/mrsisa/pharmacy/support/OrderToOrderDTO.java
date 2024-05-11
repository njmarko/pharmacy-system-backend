package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.dto.medicine.MedicineOrderInfoDTO;
import com.mrsisa.pharmacy.dto.order.OrderDTO;
import com.mrsisa.pharmacy.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderToOrderDTO extends AbstractConverter<Order, OrderDTO> {

    private final IOrderService orderService;

    @Autowired
    public OrderToOrderDTO(IOrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public OrderDTO convert(@NonNull Order order) {
        OrderDTO dto = getModelMapper().map(order, OrderDTO.class);
        dto.setOfferCount(orderService.getOfferCount(order));
        dto.setItemCount((long) order.getOrderItems().size());
        dto.setOrderItems(order.getOrderItems()
                .stream()
                .map(item -> new MedicineOrderInfoDTO(item.getId(), item.getMedicine().getId(), item.getMedicine().getName(), item.getQuantity(), item.getIsNew(), item.getMedicinePrice()))
                .collect(Collectors.toList()));
        return dto;
    }
}
