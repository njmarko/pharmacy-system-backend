package com.mrsisa.pharmacy.validation.validator.impl;

import com.mrsisa.pharmacy.dto.employee.EmployeeBusyDaysDTO;
import com.mrsisa.pharmacy.service.IAppointmentService;
import com.mrsisa.pharmacy.service.IEmploymentContractService;
import com.mrsisa.pharmacy.validation.validator.IBusyDatesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusyDatesValidator implements IBusyDatesValidator {
    private final IEmploymentContractService employmentContractService;
    private final IAppointmentService appointmentService;

    @Autowired
    public BusyDatesValidator(IEmploymentContractService employmentContractService,
                              IAppointmentService appointmentService) {
        this.employmentContractService = employmentContractService;
        this.appointmentService = appointmentService;
    }

    @Override
    public void isValid(EmployeeBusyDaysDTO employeeAppointmentSchedulingDTO) {
        Long employeeId = employeeAppointmentSchedulingDTO.getEmployeeId();
        Long pharmacyId = employeeAppointmentSchedulingDTO.getPharmacyId();
        Long patientId = employeeAppointmentSchedulingDTO.getPatientId();

        var employmentContract = employmentContractService.getContractWithPharmacy(employeeId, pharmacyId);
        appointmentService.getScheduledAppointmentForPatientAndEmployee(employmentContract.getId(), patientId);
    }
}
