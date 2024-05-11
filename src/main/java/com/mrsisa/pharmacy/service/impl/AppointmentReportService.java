package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.aggregates.AppointmentStatistics;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.domain.enums.ReportType;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.repository.IAppointmentRepository;
import com.mrsisa.pharmacy.service.IAppointmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentReportService extends ReportServiceBase implements IAppointmentReportService {

    private final IAppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentReportService(IAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<AppointmentStatistics> getAppointmentReport(Pharmacy pharmacy, LocalDate from, LocalDate to, ReportType reportType, List<EmployeeType> employeeTypes) {
        ILocalDateAdjuster adjuster = getAdjusterForReportType(reportType);
        ILabelExtractor extractor = getLabelExtractorForReportType(reportType);
        if (to.isBefore(from) || to.isEqual(from) || adjuster.getAdjusted(from).isAfter(to)) {
            throw new BusinessException("Invalid time range parameters.");
        }
        return employeeTypes.stream().map(employeeType -> getSingleAppointmentStatistics(pharmacy, from, to, employeeType, adjuster, extractor)).collect(Collectors.toList());
    }

    public AppointmentStatistics getSingleAppointmentStatistics(Pharmacy pharmacy, LocalDate from, LocalDate to, EmployeeType employeeType, ILocalDateAdjuster adjuster, ILabelExtractor extractor) {
        var statistics = new AppointmentStatistics(employeeType.toString() + " appointments");
        LocalDate lowerBound = from;
        LocalDate upperBound = adjuster.getAdjusted(from);
        while (!upperBound.isAfter(to)) {
            Long count = appointmentRepository.countAppointmentsForPharmacy(pharmacy.getId(), AppointmentStatus.TOOK_PLACE, employeeType, lowerBound.atStartOfDay(), upperBound.atStartOfDay()).orElse(0L);
            String label = extractor.getLabel(lowerBound, upperBound);
            statistics.addSample(label, count);
            lowerBound = adjuster.getAdjusted(lowerBound);
            upperBound = adjuster.getAdjusted(upperBound);
        }
        return statistics;
    }

    @Override
    public AppointmentStatistics getAppointmentBarchartReport(Pharmacy pharmacy, LocalDate from, LocalDate to, ReportType reportType) {
        if (to.isBefore(from) || to.isEqual(from)) {
            throw new BusinessException("Invalid time range parameters.");
        }
        var statistics = new AppointmentStatistics();
        Long pharmacistCount = appointmentRepository.countAppointmentsForPharmacy(pharmacy.getId(), AppointmentStatus.TOOK_PLACE, EmployeeType.PHARMACIST, from.atStartOfDay(), to.atStartOfDay()).orElse(0L);
        Long dermatologistCount = appointmentRepository.countAppointmentsForPharmacy(pharmacy.getId(), AppointmentStatus.TOOK_PLACE, EmployeeType.DERMATOLOGIST, from.atStartOfDay(), to.atStartOfDay()).orElse(0L);
        statistics.addSample("Pharmacist appointments", pharmacistCount);
        statistics.addSample("Dermatologist appointments", dermatologistCount);
        return statistics;
    }
}
