package com.mrsisa.pharmacy.dto.order;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mrsisa.pharmacy.json.deserializer.ISOLocalDateTimeDeserializer;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class OrderCreationDTO {

    @NotNull(message = "Due date cannot be null.")
    @Future(message = "Due date must be in the future.")
    @JsonDeserialize(using = ISOLocalDateTimeDeserializer.class)
    private LocalDateTime dueDate;

}
