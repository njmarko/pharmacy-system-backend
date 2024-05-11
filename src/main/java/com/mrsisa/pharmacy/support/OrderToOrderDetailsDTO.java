package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import com.mrsisa.pharmacy.dto.medicine.MedicineOrderInfoDTO;
import com.mrsisa.pharmacy.dto.order.OrderDetailsDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderToOrderDetailsDTO extends AbstractConverter<Order, OrderDetailsDTO> {
    @Override
    public OrderDetailsDTO convert(@NonNull Order order) {
        List<MedicineOrderInfoDTO> medicineOrderInfoDTOList = new ArrayList<>();
        for(MedicineOrderInfo info : order.getOrderItems()){
            medicineOrderInfoDTOList.add(new MedicineOrderInfoDTO(info.getId(), info.getMedicine().getId(), info.getMedicine().getName(), info.getQuantity(), info.getIsNew(), info.getMedicinePrice()));
        }
        return new OrderDetailsDTO(order.getId(), order.getPharmacyAdmin().getId(), order.getDueDate(), order.getPharmacy().getName(), order.getPharmacyAdmin().getUsername(), medicineOrderInfoDTOList, order.getOrderStatus());
    }
}
