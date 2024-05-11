package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.domain.aggregates.AppointmentStatistics;
import com.mrsisa.pharmacy.domain.aggregates.IncomeStatistics;
import com.mrsisa.pharmacy.domain.aggregates.MedicinePurchaseStatistics;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.enums.IncomeReportType;
import com.mrsisa.pharmacy.domain.enums.ReportType;
import com.mrsisa.pharmacy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
public class PharmacyReportController extends PharmacyControllerBase {

    private final IPharmacyReportService pharmacyReportService;
    private final IAppointmentReportService appointmentReportService;
    private final IIncomeReportService incomeReportService;

    @Autowired
    public PharmacyReportController(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService, IPharmacyReportService pharmacyReportService, IAppointmentReportService appointmentReportService, IIncomeReportService incomeReportService) {
        super(pharmacyService, pharmacyAdminService);
        this.pharmacyReportService = pharmacyReportService;
        this.appointmentReportService = appointmentReportService;
        this.incomeReportService = incomeReportService;
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/purchased-medicines")
    public List<MedicinePurchaseStatistics> getMonthlyMedicineReport(@PathVariable("id") Long id, @RequestParam("medicineIds") List<Long> medicineIds, @RequestParam(name = "from") String from, @RequestParam(name = "to") String to, @RequestParam(name = "type") ReportType reportType) {
        var pharmacy = pharmacyService.get(id);
        var fromTime = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        var toTime = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);
        return pharmacyReportService.getMedicinePurchaseStatistics(medicineIds, pharmacy, fromTime, toTime, reportType);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/purchased-medicines/barchart")
    public MedicinePurchaseStatistics getMonthlyMedicineReport(@PathVariable("id") Long id, @RequestParam(name = "from") String from, @RequestParam(name = "to") String to, @RequestParam(name = "type") ReportType reportType) {
        var pharmacy = pharmacyService.get(id);
        var fromTime = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        var toTime = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);
        return pharmacyReportService.getMedicinePurchaseBarchart(pharmacy, fromTime, toTime, reportType);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/appointments-report")
    public List<AppointmentStatistics> getAppointmentsReport(@PathVariable("id") Long id, @RequestParam(name = "from") String from, @RequestParam(name = "to") String to, @RequestParam(name = "type") ReportType reportType, @RequestParam(name = "employeeTypes") List<EmployeeType> employeeTypes) {
        var pharmacy = pharmacyService.get(id);
        var fromTime = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        var toTime = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);
        return appointmentReportService.getAppointmentReport(pharmacy, fromTime, toTime, reportType, employeeTypes);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/appointments-report/barchart")
    public AppointmentStatistics getAppointmentsReport(@PathVariable("id") Long id, @RequestParam(name = "from") String from, @RequestParam(name = "to") String to, @RequestParam(name = "type") ReportType reportType) {
        var pharmacy = pharmacyService.get(id);
        var fromTime = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        var toTime = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);
        return appointmentReportService.getAppointmentBarchartReport(pharmacy, fromTime, toTime, reportType);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/income-report")
    public List<IncomeStatistics> getIncomeReport(@PathVariable("id") Long id, @RequestParam(name = "from") String from, @RequestParam(name = "to") String to, @RequestParam(name = "type") ReportType reportType, @RequestParam(name = "incomeTypes") List<IncomeReportType> incomeReportTypes) {
        var pharmacy = pharmacyService.get(id);
        var fromTime = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        var toTime = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);
        return incomeReportService.getIncomeReport(pharmacy, fromTime, toTime, reportType, incomeReportTypes);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/income-report/barchart")
    public IncomeStatistics getIncomeReport(@PathVariable("id") Long id, @RequestParam(name = "from") String from, @RequestParam(name = "to") String to, @RequestParam(name = "type") ReportType reportType) {
        var pharmacy = pharmacyService.get(id);
        var fromTime = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        var toTime = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);
        return incomeReportService.getIncomeBarchartReport(pharmacy, fromTime, toTime, reportType);
    }
}
