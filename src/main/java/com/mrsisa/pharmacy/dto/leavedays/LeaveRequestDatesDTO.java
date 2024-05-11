package com.mrsisa.pharmacy.dto.leavedays;

import com.mrsisa.pharmacy.dto.appointment.AppointmentRangeResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeaveRequestDatesDTO {
    private List<AppointmentRangeResultDTO> appointmentDates;
    private List<LeaveRequestDateDTO> leaveRequestDateDTos;
}
