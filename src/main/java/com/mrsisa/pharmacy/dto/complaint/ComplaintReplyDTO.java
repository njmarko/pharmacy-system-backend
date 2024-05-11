package com.mrsisa.pharmacy.dto.complaint;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class ComplaintReplyDTO {
    private String content;
    private String adminUsername;

    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime datePosted;

}
