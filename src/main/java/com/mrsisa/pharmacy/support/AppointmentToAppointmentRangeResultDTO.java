package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.dto.appointment.AppointmentRangeResultDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class AppointmentToAppointmentRangeResultDTO  extends AbstractConverter<Appointment, AppointmentRangeResultDTO>
        implements IConverter<Appointment, AppointmentRangeResultDTO> {

    @Override
    public AppointmentRangeResultDTO convert(@NonNull Appointment appointment) {
        AppointmentRangeResultDTO dto = getModelMapper().map(appointment, AppointmentRangeResultDTO.class);

        dto.setPharmacyName(appointment.getEmployee().getPharmacy().getName());

        if (appointment.getPatient() != null) {
            dto.setPatientFirstName(appointment.getPatient().getFirstName());
            dto.setPatientLastName(appointment.getPatient().getLastName());
        }
        Long lengthInMinutes = ChronoUnit.MINUTES.between(appointment.getFrom(), appointment.getTo());
        dto.setLengthInMinutes(lengthInMinutes);

        return dto;
    }
}
