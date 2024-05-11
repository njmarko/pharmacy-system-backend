package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.enums.ReportType;
import com.mrsisa.pharmacy.exception.BusinessException;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public abstract class ReportServiceBase {

    protected interface ILocalDateAdjuster {
        LocalDate getAdjusted(LocalDate time);
    }

    protected interface ILabelExtractor {
        String getLabel(LocalDate from, LocalDate to);
    }

    protected final ILocalDateAdjuster monthlyAdjuster = time -> time.plusDays(1);
    protected final ILocalDateAdjuster quarterlyAdjuster = time -> time.plusWeeks(1);
    protected final ILocalDateAdjuster annualAdjuster = time -> time.plusMonths(1);

    protected final ILabelExtractor monthlyExtractor = (a, b) -> a.toString();
    protected final ILabelExtractor quarterlyExtractor = (a, b) -> String.format("Week %s", a.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()));
    protected final ILabelExtractor annualExtractor = (a, b) -> String.format("%s-%s", a.getYear(), a.getMonthValue());

    protected ILocalDateAdjuster getAdjusterForReportType(ReportType type) {
        switch (type) {
            case MONTHLY:
                return monthlyAdjuster;
            case QUARTERLY:
                return quarterlyAdjuster;
            case ANNUAL:
                return annualAdjuster;
            default:
                throw new BusinessException("Invalid report type.");
        }
    }

    protected ILabelExtractor getLabelExtractorForReportType(ReportType type) {
        switch (type) {
            case MONTHLY:
                return monthlyExtractor;
            case QUARTERLY:
                return quarterlyExtractor;
            case ANNUAL:
                return annualExtractor;
            default:
                throw new BusinessException("Invalid report type.");
        }
    }
}
