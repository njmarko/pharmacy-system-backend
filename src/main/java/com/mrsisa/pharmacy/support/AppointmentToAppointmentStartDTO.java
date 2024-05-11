package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.dto.appointment.AppointmentStartDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AppointmentToAppointmentStartDTO extends AbstractConverter<Appointment, AppointmentStartDTO>
        implements IConverter<Appointment, AppointmentStartDTO> {

    @Override
    public AppointmentStartDTO convert(@NonNull Appointment appointment) {
        return getModelMapper().map(appointment, AppointmentStartDTO.class);
    }
}