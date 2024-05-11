package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.entities.Review;
import com.mrsisa.pharmacy.dto.appointment.AppointmentDTO;
import com.mrsisa.pharmacy.service.IPharmacyEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AppointmentToAppointmentDTO extends AbstractConverter<Appointment, AppointmentDTO> implements IConverter<Appointment, AppointmentDTO> {


    private final IPharmacyEmployeeService pharmacyEmployeeService;

    @Autowired
    public AppointmentToAppointmentDTO(IPharmacyEmployeeService pharmacyEmployeeService) {
        this.pharmacyEmployeeService = pharmacyEmployeeService;
    }

    @Override
    public AppointmentDTO convert(@NonNull Appointment appointment) {
        Review review = null;
        if (appointment.getPatient() != null) {
            review = this.pharmacyEmployeeService.
                    getPatientReviewForEmployee(appointment.getPatient().getId(),
                            appointment.getEmployee().getPharmacyEmployee().getId(), appointment.getEmployee().getPharmacyEmployee().getEmployeeType());
        }
        AppointmentDTO dto = getModelMapper().map(appointment, AppointmentDTO.class);
        if (review != null) {
            dto.setPreviousRating(review.getGrade());
        }
        return dto;
    }
}
