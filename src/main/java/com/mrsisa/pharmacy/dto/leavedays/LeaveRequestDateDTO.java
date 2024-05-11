package com.mrsisa.pharmacy.dto.leavedays;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.domain.enums.LeaveDaysRequestStatus;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LeaveRequestDateDTO {
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dateFrom;
    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime dateTo;
    private LeaveDaysRequestStatus status;
}
