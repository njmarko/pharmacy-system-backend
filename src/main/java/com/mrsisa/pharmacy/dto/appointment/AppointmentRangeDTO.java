package com.mrsisa.pharmacy.dto.appointment;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AppointmentRangeDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateFrom = LocalDateTime.now().minusMonths(1);

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTo = LocalDateTime.now().plusMonths(1);

    private Long pharmacyId = -1L;
}
