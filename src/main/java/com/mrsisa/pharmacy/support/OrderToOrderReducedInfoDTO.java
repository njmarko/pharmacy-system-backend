package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.dto.order.OrderReducedInfoDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class OrderToOrderReducedInfoDTO extends AbstractConverter<Order, OrderReducedInfoDTO>{
    @Override
    public OrderReducedInfoDTO convert(@NonNull Order order) {

        return new OrderReducedInfoDTO(order.getId(), order.getOrderItems().size(), order.getDueDate(),
                order.getPharmacy().getName(), order.getPharmacyAdmin().getId(), order.getOrderStatus());
    }
}
