package com.mrsisa.pharmacy.dto;

import com.mrsisa.pharmacy.dto.appointment.AppointmentRangeResultDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveRequestDateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CalendarDatesDTO {
    private List<AppointmentRangeResultDTO> appointmentDates;
    private List<LeaveRequestDateDTO> leaveRequestDateDTos;
}
