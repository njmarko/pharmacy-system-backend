package com.mrsisa.pharmacy.dto.medicine;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.domain.enums.ReservationStatus;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateTimeDeserializer;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MedicineReservationDTO {

    private Long id;
    private Double price;

    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    private LocalDateTime reservedAt;

    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    private LocalDateTime reservationDeadline;

    private String pharmacyName;

    private List<MedicineReservationItemDTO> itemsDTO;

    private Long drugId;

    private Double rating;

    private Integer previousRating;

    private ReservationStatus status;

    private String medicineName;

    private Integer quantity;
}
