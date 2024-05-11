package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.aggregates.IncomeStatistics;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.IncomeReportType;
import com.mrsisa.pharmacy.domain.enums.ReportType;

import java.time.LocalDate;
import java.util.List;

public interface IIncomeReportService {
    List<IncomeStatistics> getIncomeReport(Pharmacy pharmacy, LocalDate from, LocalDate to, ReportType reportType, List<IncomeReportType> incomeReportTypes);

    IncomeStatistics getIncomeBarchartReport(Pharmacy pharmacy, LocalDate from, LocalDate to, ReportType reportType);
}
