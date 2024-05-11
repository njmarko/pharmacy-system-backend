package com.mrsisa.pharmacy.dto.order;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderReducedInfoDTO {

    private Long id;
    private Integer numOfMedicines;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dueDate;
    private String pharmacyName;
    private Long creatorId;
    private OrderStatus orderStatus;
}
