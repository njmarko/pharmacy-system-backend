package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.dto.*;
import com.mrsisa.pharmacy.dto.appointment.AppointmentRangeResultDTO;
import com.mrsisa.pharmacy.dto.employee.EmployeeListItemDTO;
import com.mrsisa.pharmacy.dto.employee.PharmacyEmployeeDetailsDTO;
import com.mrsisa.pharmacy.dto.leavedays.LeaveRequestDateDTO;
import com.mrsisa.pharmacy.service.*;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("api/pharmacies")
@RestController
public class PharmacyEmployeeCommonController extends PharmacyControllerBase {
    private final IPharmacyEmployeeService pharmacyEmployeeService;
    private final IEmploymentContractService employmentContractService;
    private final IAppointmentService appointmentService;
    private final ILeaveDaysRequestService leaveDaysRequestService;
    private final IConverter<EmploymentContract, PharmacyEmployeeDetailsDTO> toEmployeeDetailsDTO;
    private final IConverter<EmploymentContract, EmployeeListItemDTO> toEmployeeListItemDTO;
    private final IConverter<Appointment, AppointmentRangeResultDTO> toAppointmentRangeDTO;
    private final IConverter<LeaveDaysRequest, LeaveRequestDateDTO> toLeaveRequestDateDTO;

    @Autowired
    public PharmacyEmployeeCommonController(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService, IPharmacyEmployeeService pharmacyEmployeeService, IEmploymentContractService employmentContractService, IAppointmentService appointmentService, ILeaveDaysRequestService leaveDaysRequestService, IConverter<EmploymentContract, PharmacyEmployeeDetailsDTO> toEmployeeDetailsDTO, IConverter<EmploymentContract, EmployeeListItemDTO> toEmployeeListItemDTO, IConverter<Appointment, AppointmentRangeResultDTO> toAppointmentRangeDTO, IConverter<LeaveDaysRequest, LeaveRequestDateDTO> toLeaveRequestDateDTO) {
        super(pharmacyService, pharmacyAdminService);
        this.pharmacyEmployeeService = pharmacyEmployeeService;
        this.employmentContractService = employmentContractService;
        this.appointmentService = appointmentService;
        this.leaveDaysRequestService = leaveDaysRequestService;
        this.toEmployeeDetailsDTO = toEmployeeDetailsDTO;
        this.toEmployeeListItemDTO = toEmployeeListItemDTO;
        this.toAppointmentRangeDTO = toAppointmentRangeDTO;
        this.toLeaveRequestDateDTO = toLeaveRequestDateDTO;
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/employees/all")
    public List<EmployeeListItemDTO> getAllEmployees(@PathVariable("id") Long id) {
        var pharmacy = getOr404(id);
        List<EmploymentContract> employees = pharmacyEmployeeService.getAllPharmacyEmployees(pharmacy);
        return (List<EmployeeListItemDTO>) toEmployeeListItemDTO.convert(employees);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "pharmacyId")
    @GetMapping(value = "/{pharmacyId}/employees/{employeeId}")
    public PharmacyEmployeeDetailsDTO getEmployeeDetails(@PathVariable("pharmacyId") Long pharmacyId, @PathVariable("employeeId") Long employeeId) {
        var pharmacy = getOr404(pharmacyId);
        var contract = employmentContractService.getPharmacyEmployee(pharmacy.getId(), employeeId);
        return toEmployeeDetailsDTO.convert(contract);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "pharmacyId")
    @GetMapping(value = "/{pharmacyId}/employees/{employeeId}/calendar")
    public CalendarDatesDTO getPharmacyEmployeeCalendar(@PathVariable("pharmacyId") Long pharmacyId,
                                                        @PathVariable("employeeId") Long employeeId,
                                                        @RequestParam("dateFrom") String fromTime,
                                                        @RequestParam("dateTo") String toTime) {
        var pharmacy = getOr404(pharmacyId);
        var contract = employmentContractService.getPharmacyEmployee(pharmacy.getId(), employeeId);
        List<Appointment> appointments = appointmentService.getAppointmentsForEmployee(pharmacyId, contract.getPharmacyEmployee().getId(), LocalDateTime.parse(fromTime, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse(toTime, DateTimeFormatter.ISO_DATE_TIME), contract.getPharmacyEmployee().getEmployeeType());
        List<LeaveDaysRequest> leaveRequests = leaveDaysRequestService.getAllPendingAndAcceptedLeaveDaysRequestForEmployee(employeeId);
        return new CalendarDatesDTO(
                (List<AppointmentRangeResultDTO>) toAppointmentRangeDTO.convert(appointments),
                (List<LeaveRequestDateDTO>) toLeaveRequestDateDTO.convert(leaveRequests)
        );
    }
}
