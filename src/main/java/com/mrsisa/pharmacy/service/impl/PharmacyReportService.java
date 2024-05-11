package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.aggregates.MedicinePurchaseStatistics;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.ReportType;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.repository.IMedicinePurchaseRepository;
import com.mrsisa.pharmacy.repository.IMedicineRepository;
import com.mrsisa.pharmacy.repository.IMedicineStockRepository;
import com.mrsisa.pharmacy.service.IPharmacyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PharmacyReportService extends ReportServiceBase implements IPharmacyReportService {

    private final IMedicineRepository medicineRepository;
    private final IMedicinePurchaseRepository medicinePurchaseRepository;
    private final IMedicineStockRepository medicineStockRepository;

    @Autowired
    public PharmacyReportService(IMedicineRepository medicineRepository, IMedicinePurchaseRepository medicinePurchaseRepository, IMedicineStockRepository medicineStockRepository) {
        this.medicineRepository = medicineRepository;
        this.medicinePurchaseRepository = medicinePurchaseRepository;
        this.medicineStockRepository = medicineStockRepository;
    }
    @Override
    public List<MedicinePurchaseStatistics> getMedicinePurchaseStatistics(List<Long> medicineIds, Pharmacy pharmacy, LocalDate from, LocalDate to, ReportType reportType) {
        ILocalDateAdjuster adjuster = getAdjusterForReportType(reportType);
        ILabelExtractor extractor = getLabelExtractorForReportType(reportType);
        if (to.isBefore(from) || to.isEqual(from) || adjuster.getAdjusted(from).isAfter(to)) {
            throw new BusinessException("Invalid time range parameters.");
        }
        return medicineIds.stream().map(id -> this.getSingleMedicineStatistics(id, pharmacy, from, to, adjuster, extractor)).collect(Collectors.toList());
    }

    public MedicinePurchaseStatistics getSingleMedicineStatistics(Long medicineId, Pharmacy pharmacy, LocalDate from, LocalDate to, ILocalDateAdjuster adjuster, ILabelExtractor extractor) {
        var medicine = medicineRepository.findByIdAndActiveTrue(medicineId);
        if (medicine == null) {
            throw new BusinessException("Cannot find medicine with id: " + medicineId);
        }
        var statistics = new MedicinePurchaseStatistics(medicine.getName());
        LocalDate upperBound = adjuster.getAdjusted(from);
        LocalDate lowerBound = from;
        while (!upperBound.isAfter(to)) {
            Integer count = medicinePurchaseRepository.getMedicinePurchaseCount(medicineId, pharmacy.getId(), lowerBound, upperBound).orElse(0);
            String label = extractor.getLabel(lowerBound, upperBound);
            statistics.addSample(label, count);
            lowerBound = adjuster.getAdjusted(lowerBound);
            upperBound = adjuster.getAdjusted(upperBound);
        }
        return statistics;
    }

    @Override
    public MedicinePurchaseStatistics getMedicinePurchaseBarchart(Pharmacy pharmacy, LocalDate from, LocalDate to, ReportType reportType) {
        if (to.isBefore(from) || to.isEqual(from)) {
            throw new BusinessException("Invalid time range parameters.");
        }
        var statistics = new MedicinePurchaseStatistics();
        medicineStockRepository.getPharmacyStocksStream(pharmacy.getId()).forEach(stock -> {
            Integer count = medicinePurchaseRepository.getMedicinePurchaseCount(stock.getMedicine().getId(), pharmacy.getId(), from, to).orElse(0);
            String label = stock.getMedicine().getName();
            statistics.addSample(label, count);
        });
        return statistics;
    }
}
