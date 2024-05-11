package com.mrsisa.pharmacy.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import com.mrsisa.pharmacy.dto.medicine.MedicineOrderInfoDTO;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsDTO {

    private Long id;
    private Long pharmacyAdminId;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dueDate;
    private String pharmacyName;
    private String pharmacyAdmin;
    private List<MedicineOrderInfoDTO> orderItems;
    private OrderStatus orderStatus;

}
