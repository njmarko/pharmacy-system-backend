package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.dto.appointment.AppointmentDatesDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AppointmentToAppointmentDatesDTO extends AbstractConverter<Appointment, AppointmentDatesDTO>
        implements IConverter<Appointment, AppointmentDatesDTO> {

    @Override
    public AppointmentDatesDTO convert(@NonNull Appointment appointment) {
        return getModelMapper().map(appointment, AppointmentDatesDTO.class);
    }
}
