package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.dto.appointment.AppointmentDTO;
import com.mrsisa.pharmacy.dto.appointment.AppointmentRangeResultDTO;
import com.mrsisa.pharmacy.dto.appointment.AvailableAppointmentCreationDTO;
import com.mrsisa.pharmacy.dto.appointment.UpdateAppointmentPriceDTO;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyDTO;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacySearchDTO;
import com.mrsisa.pharmacy.service.IAppointmentService;
import com.mrsisa.pharmacy.service.IPharmacyAdminService;
import com.mrsisa.pharmacy.service.IPharmacyService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("/api/pharmacies")
@RestController
public class PharmacyAppointmentController extends PharmacyControllerBase {
    private final IAppointmentService appointmentService;
    private final IConverter<Appointment, AppointmentDTO> toAppointmentDTO;
    private final IConverter<Appointment, AppointmentRangeResultDTO> toAppointmentRangeDTO;
    private final IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO;

    @Autowired
    public PharmacyAppointmentController(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService, IAppointmentService appointmentService, IConverter<Appointment, AppointmentDTO> toAppointmentDTO, IConverter<Appointment, AppointmentRangeResultDTO> toAppointmentRangeDTO, IConverter<Pharmacy, PharmacyDTO> toPharmacyDTO) {
        super(pharmacyService, pharmacyAdminService);
        this.appointmentService = appointmentService;
        this.toAppointmentDTO = toAppointmentDTO;
        this.toAppointmentRangeDTO = toAppointmentRangeDTO;
        this.toPharmacyDTO = toPharmacyDTO;
    }

    @GetMapping(value = "/{id}/dermatologist-appointments/all")
    public List<AppointmentRangeResultDTO> getPharmacyAvailableDermatologistAppointments(@PathVariable("id") Long id, @RequestParam("from") String fromTime, @RequestParam("to") String toTime) {
        var pharmacy = getOr404(id);
        List<Appointment> appointmentPage = appointmentService.getAvailableDermatologistAppointmentsForPharmacy(pharmacy,
                LocalDateTime.parse(fromTime, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse(toTime, DateTimeFormatter.ISO_DATE_TIME));
        return (List<AppointmentRangeResultDTO>) toAppointmentRangeDTO.convert(appointmentPage);
    }

    @GetMapping(value = "/all-with-pharmacist-appointments-at-datetime")
    public Page<PharmacyDTO> getPharmaciesWithAvailablePharmacistAppointmentsOnSpecifiedDateAndTime(@Valid PharmacySearchDTO searchDTO,
                                                                                                    @PageableDefault Pageable pageAndSortParams) {
        return pharmacyService.getPharmaciesWithAvailablePharmacistAppointmentsOnSpecifiedDateAndtime(searchDTO.getName(),
                searchDTO.getCity(),
                searchDTO.getGradeLow(),
                searchDTO.getGradeHigh(),
                searchDTO.getUserLatitude(), searchDTO.getUserLongitude(), searchDTO.getDistance(),
                searchDTO.getDateTime(), pageAndSortParams)
                .map(toPharmacyDTO::convert);
    }

    @GetMapping(value = "/{id}/available-pharmacist-appointments-at-datetime")
    public Page<AppointmentDTO> getAvailablePharmacistAppointmentsOnSpecifiedDateAndTime(@PathVariable("id") Long id,
                                                                                         @RequestParam(value = "name", defaultValue = "") String name,
                                                                                         @RequestParam(value = "dateTime") String dateTime,
                                                                                         @PageableDefault Pageable pageable) {
        return appointmentService.getAvailablePharmacistAppointmentsForPharmacyOnSpecifiedDateAndTime(id, name, dateTime, pageable)
                .map(toAppointmentDTO::convert);
    }

    @GetMapping(value = "/{id}/dermatologist-appointments")
    public Page<AppointmentDTO> getAvailableDermatologistAppointments(@PathVariable("id") Long id, @PageableDefault Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<Appointment> appointmentPage = appointmentService.getAvailableDermatologistAppointmentsForPharmacy(pharmacy, pageable);
        return appointmentPage.map(toAppointmentDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PutMapping(value = "/{id}/appointments/price")
    public PharmacyDTO updateAppointmentPrice(@PathVariable("id") Long id, @Valid @RequestBody UpdateAppointmentPriceDTO updateAppointmentPriceDTO) {
        var updated = pharmacyService.updateAppointmentPrices(id, updateAppointmentPriceDTO.getPharmacistAppointmentPrice(), updateAppointmentPriceDTO.getDermatologistAppointmentPrice());
        return toPharmacyDTO.convert(updated);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PostMapping(value = "/{id}/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDTO createAvailableAppointment(@PathVariable("id") Long id, @Valid @RequestBody AvailableAppointmentCreationDTO dto) {
        var created = appointmentService.createAvailableAppointment(id, dto.getEmployeeId(), dto.getFromTime(), dto.getToTime());
        return toAppointmentDTO.convert(created);
    }
}
