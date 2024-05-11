package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.aggregates.AppointmentStatistics;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.enums.ReportType;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentReportService {
    List<AppointmentStatistics> getAppointmentReport(Pharmacy pharmacy, LocalDate fromTime, LocalDate toTime, ReportType reportType, List<EmployeeType> employeeTypes);

    AppointmentStatistics getAppointmentBarchartReport(Pharmacy pharmacy, LocalDate fromTime, LocalDate toTime, ReportType reportType);
}
