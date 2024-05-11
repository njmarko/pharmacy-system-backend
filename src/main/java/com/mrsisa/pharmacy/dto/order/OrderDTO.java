package com.mrsisa.pharmacy.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import com.mrsisa.pharmacy.dto.medicine.MedicineOrderInfoDTO;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {

    private Long id;
    private Long pharmacyAdminId;
    private String pharmacyAdminUsername;
    private String pharmacyAdminFirstName;
    private String pharmacyAdminLastName;
    private String pharmacyAdminEmail;
    private String pharmacyName;
    private OrderStatus orderStatus;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dueDate;
    private Long itemCount;
    private Long offerCount;
    List<MedicineOrderInfoDTO> orderItems;

}
