package com.mrsisa.pharmacy.validation.validator.impl;

import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.dto.appointment.AppointmentConclusionDTO;
import com.mrsisa.pharmacy.service.IAppointmentService;
import com.mrsisa.pharmacy.service.IEmploymentContractService;
import com.mrsisa.pharmacy.service.IMedicineStockService;
import com.mrsisa.pharmacy.validation.validator.IAppointmentConclusionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Component
public class AppointmentConclusionValidator implements IAppointmentConclusionValidator {
    private final IAppointmentService appointmentService;
    private final IMedicineStockService medicineStockService;
    private final IEmploymentContractService employmentContractService;

    @Autowired
    public AppointmentConclusionValidator(IAppointmentService appointmentService,
                                          IMedicineStockService medicineStockService,
                                          IEmploymentContractService employmentContractService) {
        this.appointmentService = appointmentService;
        this.medicineStockService = medicineStockService;
        this.employmentContractService = employmentContractService;
    }

    @Override
    public void isValid(AppointmentConclusionDTO appointmentConclusionDTO) {
        checkValidityOfAppointment(appointmentConclusionDTO);

        checkValidityOfMedicineStocks(appointmentConclusionDTO);
    }

    private void checkValidityOfAppointment(AppointmentConclusionDTO appointmentConclusionDTO) {
        var appointment = appointmentService.get(appointmentConclusionDTO.getAppointmentId());
        var employmentContract = employmentContractService.getContractWithPharmacy(appointmentConclusionDTO.getEmployeeId(),
                appointmentConclusionDTO.getPharmacyId());

        if (appointment.getAppointmentStatus() != AppointmentStatus.BOOKED || appointment.getFrom().isAfter(LocalDateTime.now()) ||
                appointment.getTo().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is bad!");
        }

        if (!appointment.getEmployee().getId().equals(employmentContract.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is not your appointment!");
        }

        if (!appointment.getPatient().getId().equals(appointmentConclusionDTO.getPatientId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This patient is not in this appointment!");
        }
    }

    private void checkValidityOfMedicineStocks(AppointmentConclusionDTO appointmentConclusionDTO) {
        for (var medicineStockDTO: appointmentConclusionDTO.getMedicineStocks()) {
            var medicineStock = medicineStockService.get(medicineStockDTO.getMedicineStockId());

            if (!medicineStock.getPharmacy().getId().equals(appointmentConclusionDTO.getPharmacyId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This medicine does not exist in this pharmacy!");
            }

            if (medicineStock.getQuantity() < medicineStockDTO.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity is greater than available quantity");
            }

        }
    }
}
