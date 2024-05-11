package com.mrsisa.pharmacy.dto.complaint;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ComplaintDTO {
    private Long complaintId;
    private String title;
    private Long patientId;
    private ComplaintReplyDTO reply;
    private String complaintType;
    private String entity;

    @JsonSerialize(using = ISOLocalDateTimeSerializer.class)
    private LocalDateTime datePosted;

    public ComplaintDTO(Long complaintId, String title, Long patientId, ComplaintReplyDTO reply, String complaintType, String entity, LocalDateTime datePosted) {
        this.complaintId = complaintId;
        this.title = title;
        this.patientId = patientId;
        this.reply = reply;
        this.complaintType = complaintType;
        this.entity = entity;
        this.datePosted = datePosted;
    }
}
