package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.PromotionStatus;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.*;
import com.mrsisa.pharmacy.service.IMedicineStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MedicineStockService extends JPAService<MedicineStock> implements IMedicineStockService {
    private final IMedicineStockRepository medicineStockRepository;
    private final IMedicineReservationRepository medicineReservationRepository;
    private final IStockPriceRepository stockPriceRepository;
    private final IOrderRepository orderRepository;
    private final IPharmacyRepository pharmacyRepository;
    private final IOrderItemRepository orderItemRepository;
    private final IPatientRepository patientRepository;
    private final IPromotionItemRepository promotionItemRepository;

    private final Logger log = LoggerFactory.getLogger(MedicineStockService.class);

    @Autowired
    public MedicineStockService(IMedicineStockRepository medicineStockRepository,
                                IMedicineReservationRepository medicineReservationRepository,
                                IStockPriceRepository stockPriceRepository,
                                IOrderRepository orderRepository,
                                IPharmacyRepository pharmacyRepository,
                                IOrderItemRepository orderItemRepository,
                                IPatientRepository patientRepository, IPromotionItemRepository promotionItemRepository) {
        this.medicineStockRepository = medicineStockRepository;
        this.medicineReservationRepository = medicineReservationRepository;
        this.stockPriceRepository = stockPriceRepository;
        this.orderRepository = orderRepository;
        this.pharmacyRepository = pharmacyRepository;
        this.orderItemRepository = orderItemRepository;
        this.patientRepository = patientRepository;
        this.promotionItemRepository = promotionItemRepository;
    }

    @Override
    protected JpaRepository<MedicineStock, Long> getEntityRepository() {
        return medicineStockRepository;
    }

    @Override
    public Page<MedicineStock> getAllMedicinesForPharmacyAndPatientIsNotAllergicTo(Long pharmacyId, Long patientId,
                                                                                   String medicineName, List<Long> chosenMedicineIds,
                                                                                   Pageable pageable) {
        String medicineNameParam = "%" + medicineName + "%";
        var patient = getIfPatientExists(patientId);

        List<Medicine> allergicMedicines = new ArrayList<>(patient.getAllergicTo());

        if (chosenMedicineIds.isEmpty()) {
            chosenMedicineIds.add(-1L);
        }

        if (allergicMedicines.isEmpty()) {
            allergicMedicines.add(null);
        }

        return medicineStockRepository.getMedicinesForPharmacyPatientIsNotAllergicTo(pharmacyId, medicineNameParam,
                chosenMedicineIds, allergicMedicines, pageable);
    }

    @Override
    public Page<MedicineStock> getReplacementMedicinesPatientIsNotAllergicTo(Long pharmacyId, Long patientId, Long medicineStockId,
                                                                             String medicineName, List<Long> chosenMedicineIds,
                                                                             Pageable pageable) {
        String medicineNameParam = "%" + medicineName + "%";
        var patient = getIfPatientExists(patientId);

        var medicineStock = this.get(medicineStockId);

        if (chosenMedicineIds.isEmpty()) {
            chosenMedicineIds.add(-1L);
        }


        List<Medicine> allergicMedicines = new ArrayList<>(patient.getAllergicTo());

        if (allergicMedicines.isEmpty()) {
            allergicMedicines.add(null);
        }

        List<Long> replacementMedicines = medicineStock.getMedicine().getReplacements().stream().map(BaseEntity::getId).collect(Collectors.toList());


        return medicineStockRepository.getReplacementMedicinesForPharmacyPatientIsNotAllergicTo(pharmacyId,
                allergicMedicines, replacementMedicines, medicineNameParam, chosenMedicineIds, pageable);
    }

    @Override
    public List<MedicineStock> getPharmacyStockList(Pharmacy pharmacy) {
        return medicineStockRepository.getPharmacyStocksList(pharmacy.getId());
    }

    @Override
    public Page<MedicineStock> getPharmacyStocksNotInPromotion(Pharmacy pharmacy, String name, List<Long> medicineIds, Pageable pageable) {
        return medicineStockRepository.getPharmacyStocksNotInPromotion(pharmacy.getId(), name, medicineIds, pageable);
    }

    @Override
    public Page<MedicineStock> getMedicineStocksForPharmacy(Pharmacy pharmacy, String medicineName, Pageable pageable) {
        String medicineNameParam = "%" + medicineName + "%";
        return medicineStockRepository.getMedicineStocksForPharmacy(pharmacy.getId(), medicineNameParam, pageable);
    }

    @Override
    public Page<MedicineStock> getAvailableMedicinesForPharmacy(Pharmacy pharmacy, Pageable pageable) {
        return medicineStockRepository.getAvailableMedicineStocksForPharmacy(pharmacy.getId(), pageable);
    }

    @Override
    public Page<MedicineStock> getAvailableMedicineStocksForMedicine(Long medicineId, Pageable pageable) {
        return this.medicineStockRepository.getAvailableMedicineStocksForMedicine(medicineId, pageable);
    }

    @Override
    public MedicineStock registerMedicineInPharmacy(Long pharmacyId, Medicine medicine, double price, int quantity) {
        log.info("Starting register medicine in pharmacy...");
        var pharmacy = pharmacyRepository.findByIdAndActiveTrue(pharmacyId).orElseThrow(() -> new NotFoundException("Cannot find pharmacy with id: " + pharmacyId));
        medicineStockRepository.getByMedicineCodeForPharmacy(medicine.getCode().toLowerCase(), pharmacy.getId()).ifPresent(stock -> {
            throw new BusinessException("Medicine is already registered in this pharmacy.");
        });
        var stock = new MedicineStock(quantity, pharmacy, medicine);
        stock.addPriceTag(new StockPrice(price, false, stock));
        pharmacy.getMedicineStocks().add(stock);
        save(stock);
        // Find all the order where this medicine is considered as new
        Stream<MedicineOrderInfo> orderItems = orderItemRepository.getItemsWithMedicine(pharmacyId, medicine.getId());
        orderItems.forEach(item -> {
            item.setMedicinePrice(0.0);
            item.setIsNew(false);
        });
        log.info("Finished register medicine in pharmacy...");
        return stock;
    }

    @Override
    public MedicineStock getMedicineStockForPharmacy(Long pharmacyId, Long medicineId) {
        return this.medicineStockRepository.getMedicineInPharmacy(pharmacyId, medicineId).orElseThrow(() -> new NotFoundException("Cannot find medicine stock for medicine with id: " + medicineId + " in pharmacy with id: " + pharmacyId));
    }

    @Override
    public void removeMedicine(Pharmacy pharmacy, MedicineStock medicineStock) {
        Stream<MedicineReservation> pharmacyReservations = medicineReservationRepository.getFutureReservationsForPharmacyWithMedicine(pharmacy.getId(), medicineStock.getMedicine().getId(), LocalDateTime.now().plusDays(1));
        pharmacyReservations.findAny().ifPresent(medicineReservation -> {
            throw new BusinessException("Medicine is already reserved and can't be deleted.");
        });
        Stream<MedicineOrderInfo> orderItems = orderItemRepository.getItemsWithMedicine(pharmacy.getId(), medicineStock.getMedicine().getId());
        orderItems.findAny().ifPresent(item -> {
            throw new BusinessException("Medicine is already included in order and can't be deleted.");
        });
        medicineStock.setActive(false);
        save(medicineStock);
    }

    @Override
    public MedicineStock updateStock(Long pharmacyId, Long stockId, Double newPrice) {
        var medicineStock = getStockInPharmacy(pharmacyId, stockId);
        // Check if stock is currently on promotion
        promotionItemRepository.getItemsWithMedicineInPharmacyStream(pharmacyId, medicineStock.getMedicine().getId(), PromotionStatus.ACTIVE, LocalDate.now()).findAny().ifPresent(item -> {
            throw new BusinessException("Medicine is already on the promotion and it's price can't be updated.");
        });
        // Remove current active price
        stockPriceRepository.findActiveForStock(stockId).ifPresent(PriceTag::deprecate);
        medicineStock.addPriceTag(new StockPrice(newPrice, false, medicineStock));
        save(medicineStock);
        return medicineStock;
    }

    @Override
    public MedicineStock getStockInPharmacy(Long pharmacyId, Long stockId) {
        return medicineStockRepository.findByIdInPharmacy(stockId, pharmacyId).orElseThrow(() -> new NotFoundException("Cannot find medicine stock with id: " + stockId));
    }

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public void updatePharmacyStock(Long pharmacyId, Long orderId) {
        log.info("Starting update pharmacy stock...");
        var order = orderRepository.getOrderDetails(orderId).orElseThrow(() -> new NotFoundException("Cannot find order with id: " + orderId));
        if (!order.getPharmacy().getId().equals(pharmacyId)) {
            throw new NotFoundException("Order does not belong to pharmacy with id: " + pharmacyId);
        }
        order.getOrderItems().forEach(item -> {
            if (Boolean.TRUE.equals(item.getIsNew())) {
                registerMedicineInPharmacy(pharmacyId, item.getMedicine(), item.getMedicinePrice(), item.getQuantity());
            } else {
                MedicineStock stock = medicineStockRepository.getMedicineInPharmacy(pharmacyId, item.getMedicine().getId()).orElseThrow(() -> new BusinessException("Medicine " + item.getMedicine().getName() + " is not registered in this pharmacy."));
                stock.setQuantity(stock.getQuantity() + item.getQuantity());
            }
        });
        log.info("Finished update pharmacy stock...");
    }

    @Override
    public Boolean isMedicineRegisteredInPharmacy(Pharmacy pharmacy, Medicine medicine) {
        return medicineStockRepository.getMedicineInPharmacy(pharmacy.getId(), medicine.getId()).isPresent();
    }

    private Patient getIfPatientExists(Long patientId) {
        var patient = patientRepository.findById(patientId).orElseThrow(() -> new NotFoundException("Cannot find patient with id: " + patientId));
        if (Boolean.FALSE.equals(patient.getActive())) {
            throw new NotFoundException("Cannot find patient with id: " + patientId);
        }

        return patient;
    }

}
