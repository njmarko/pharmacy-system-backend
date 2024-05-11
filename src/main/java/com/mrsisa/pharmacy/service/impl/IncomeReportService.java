package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.aggregates.IncomeStatistics;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.enums.IncomeReportType;
import com.mrsisa.pharmacy.domain.enums.ReportType;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.repository.IAppointmentRepository;
import com.mrsisa.pharmacy.repository.IMedicinePurchaseRepository;
import com.mrsisa.pharmacy.service.IIncomeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class IncomeReportService extends ReportServiceBase implements IIncomeReportService {

    private final IAppointmentRepository appointmentRepository;
    private final IMedicinePurchaseRepository medicinePurchaseRepository;

    @Autowired
    public IncomeReportService(IAppointmentRepository appointmentRepository, IMedicinePurchaseRepository medicinePurchaseRepository) {
        this.appointmentRepository = appointmentRepository;
        this.medicinePurchaseRepository = medicinePurchaseRepository;
    }

    @Override
    public List<IncomeStatistics> getIncomeReport(Pharmacy pharmacy, LocalDate from, LocalDate to, ReportType reportType, List<IncomeReportType> incomeReportTypes) {
        ILocalDateAdjuster adjuster = getAdjusterForReportType(reportType);
        ILabelExtractor extractor = getLabelExtractorForReportType(reportType);
        if (to.isBefore(from) || to.isEqual(from) || adjuster.getAdjusted(from).isAfter(to)) {
            throw new BusinessException("Invalid time range parameters.");
        }
        return incomeReportTypes.stream().map(incomeReportType -> getSingleIncome(pharmacy, from, to, incomeReportType, adjuster, extractor)).collect(Collectors.toList());
    }

    public IncomeStatistics getSingleIncome(Pharmacy pharmacy, LocalDate from, LocalDate to, IncomeReportType incomeReportType, ILocalDateAdjuster adjuster, ILabelExtractor extractor) {
        var statistics = new IncomeStatistics(incomeReportType.toString());
        LocalDate lowerBound = from;
        LocalDate upperBound = adjuster.getAdjusted(from);
        while (!upperBound.isAfter(to)) {
            Double price = 0.0;
            if (incomeReportType == IncomeReportType.PHARMACIST_APPOINTMENT || incomeReportType == IncomeReportType.DERMATOLOGIST_APPOINTMENT) {
                price = appointmentRepository.getAppointmentsIncome(pharmacy.getId(), AppointmentStatus.TOOK_PLACE, getEmployeeTypeForIncomeType(incomeReportType), lowerBound.atStartOfDay(), upperBound.atStartOfDay()).orElse(0.0);
            } else if (incomeReportType == IncomeReportType.MEDICINE_SALES) {
                price = medicinePurchaseRepository.getIncomeFromMedicines(pharmacy.getId(), lowerBound, upperBound).orElse(0.0);
            }
            String label = extractor.getLabel(lowerBound, upperBound);
            statistics.addSample(label, price);
            lowerBound = adjuster.getAdjusted(lowerBound);
            upperBound = adjuster.getAdjusted(upperBound);
        }
        return statistics;
    }

    @Override
    public IncomeStatistics getIncomeBarchartReport(Pharmacy pharmacy, LocalDate from, LocalDate to, ReportType reportType) {
        if (to.isBefore(from) || to.isEqual(from)) {
            throw new BusinessException("Invalid time range parameters.");
        }
        var statistics = new IncomeStatistics();
        Double pharmacistPrice = appointmentRepository.getAppointmentsIncome(pharmacy.getId(), AppointmentStatus.TOOK_PLACE, EmployeeType.PHARMACIST, from.atStartOfDay(), to.atStartOfDay()).orElse(0.0);
        Double dermatologistPrice = appointmentRepository.getAppointmentsIncome(pharmacy.getId(), AppointmentStatus.TOOK_PLACE, EmployeeType.DERMATOLOGIST, from.atStartOfDay(), to.atStartOfDay()).orElse(0.0);
        Double medicinePrice = medicinePurchaseRepository.getIncomeFromMedicines(pharmacy.getId(), from, to).orElse(0.0);
        statistics.addSample("Pharmacist appointments", pharmacistPrice);
        statistics.addSample("Dermatologist appointments", dermatologistPrice);
        statistics.addSample("Medicine sales", medicinePrice);
        return statistics;
    }

    public EmployeeType getEmployeeTypeForIncomeType(IncomeReportType type) {
        if (type == IncomeReportType.PHARMACIST_APPOINTMENT) {
            return EmployeeType.PHARMACIST;
        }
        return EmployeeType.DERMATOLOGIST;
    }
}
