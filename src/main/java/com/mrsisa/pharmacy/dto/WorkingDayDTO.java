package com.mrsisa.pharmacy.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalTimeDeserializer;
import com.mrsisa.pharmacy.json.serializer.ISOLocalTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingDayDTO {

    private DayOfWeek day;

    @JsonSerialize(using = ISOLocalTimeSerializer.class)
    @JsonDeserialize(using = ISOLocalTimeDeserializer.class)
    private LocalTime fromHours;

    @JsonSerialize(using = ISOLocalTimeSerializer.class)
    @JsonDeserialize(using = ISOLocalTimeDeserializer.class)
    private LocalTime toHours;

}
