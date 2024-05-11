package com.mrsisa.pharmacy.dto;

import com.mrsisa.pharmacy.dto.appointment.AppointmentDatesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BusyDatesDTO {
    private List<WorkingDayTimeDTO> workingDays;
    private List<AppointmentDatesDTO> appointmentDates;
}
