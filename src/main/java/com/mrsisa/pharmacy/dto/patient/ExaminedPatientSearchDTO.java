package com.mrsisa.pharmacy.dto.patient;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ExaminedPatientSearchDTO {
    private String firstName;
    private String lastName;
    private String from;
    private String to;

    public LocalDateTime getFromTime() {
        if (from == null || from.equals("")) {
            return null;
        }
        return LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalDateTime getToTime() {
        if (to == null || to.equals("")) {
            return null;
        }
        return LocalDateTime.parse(to, DateTimeFormatter.ISO_DATE_TIME);
    }
}
