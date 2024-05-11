package com.mrsisa.pharmacy.dto.medicine;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateTimeDeserializer;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class DrugReservationDTO {

    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    @NotNull(message = "Date and Time for when the reservation was made must be specified")
    private LocalDateTime reservedAt;

    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    @NotNull(message = "Reservation pickup Date must be specified")
    private LocalDateTime reservationDeadline;

    @NotNull(message = "Pharmacy must be specified")
    private Long pharmacyId;

    @NotNull(message = "Drug must be specified")
    private Long medicineId;

    @NotNull(message = "Quantity must be specified")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

}
