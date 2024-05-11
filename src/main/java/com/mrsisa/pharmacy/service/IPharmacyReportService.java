package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.aggregates.MedicinePurchaseStatistics;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.ReportType;

import java.time.LocalDate;
import java.util.List;

public interface IPharmacyReportService {
    List<MedicinePurchaseStatistics> getMedicinePurchaseStatistics(List<Long> medicineIds, Pharmacy pharmacy, LocalDate fromTime, LocalDate toTime, ReportType reportType);
    MedicinePurchaseStatistics getMedicinePurchaseBarchart(Pharmacy pharmacy, LocalDate fromTime, LocalDate toTime, ReportType reportType);
}
