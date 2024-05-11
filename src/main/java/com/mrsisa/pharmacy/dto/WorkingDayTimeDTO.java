package com.mrsisa.pharmacy.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.serializer.ISOLocalTimeSerializer;
import lombok.Data;

import java.time.LocalTime;

@Data
public class WorkingDayTimeDTO {
    private int dayInWeek;
    @JsonSerialize(using = ISOLocalTimeSerializer.class)
    private LocalTime fromHours;
    @JsonSerialize(using = ISOLocalTimeSerializer.class)
    private LocalTime toHours;
}
