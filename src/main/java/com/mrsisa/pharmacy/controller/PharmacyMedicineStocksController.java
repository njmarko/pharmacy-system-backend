package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.dto.medicine.MedicineReducedDTO;
import com.mrsisa.pharmacy.dto.promotion.PromotionStocksSearchDTO;
import com.mrsisa.pharmacy.dto.stock.MedicineStockDTO;
import com.mrsisa.pharmacy.dto.stock.MedicineStockRegistrationDTO;
import com.mrsisa.pharmacy.dto.stock.UpdateMedicineStockDTO;
import com.mrsisa.pharmacy.service.IMedicineService;
import com.mrsisa.pharmacy.service.IMedicineStockService;
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
import java.util.Collection;

@RestController
@RequestMapping("api/pharmacies")
public class PharmacyMedicineStocksController extends PharmacyControllerBase {
    private final IMedicineStockService medicineStockService;
    private final IMedicineService medicineService;
    private final IConverter<MedicineStock, MedicineStockDTO> toMedicineStockDTO;
    private final IConverter<MedicineStock, MedicineReducedDTO> toMedicineReducedDTO;

    @Autowired
    public PharmacyMedicineStocksController(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService, IMedicineStockService medicineStockService, IMedicineService medicineService, IConverter<MedicineStock, MedicineStockDTO> toMedicineStockDTO, IConverter<MedicineStock, MedicineReducedDTO> toMedicineReducedDTO) {
        super(pharmacyService, pharmacyAdminService);
        this.medicineStockService = medicineStockService;
        this.medicineService = medicineService;
        this.toMedicineStockDTO = toMedicineStockDTO;
        this.toMedicineReducedDTO = toMedicineReducedDTO;
    }

    @GetMapping(value = "/{id}/stocks/available")
    public Page<MedicineStockDTO> getPharmacyMedicineStocks(@PathVariable("id") Long id, @PageableDefault Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<MedicineStock> stockPage = medicineStockService.getAvailableMedicinesForPharmacy(pharmacy, pageable);
        return stockPage.map(toMedicineStockDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/stocks/not-selected-in-promotion")
    public Page<MedicineStockDTO> getMedicineStocksNotInCurrentPromotion(@PathVariable("id") Long id, PromotionStocksSearchDTO dto, @PageableDefault Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<MedicineStock> stockPage = medicineStockService.getPharmacyStocksNotInPromotion(pharmacy, dto.getName(), dto.getFixedMedicineIds(), pageable);
        return stockPage.map(toMedicineStockDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping("/{id}/stocks")
    public Page<MedicineStockDTO> getAllMedicineStocks(@PathVariable("id") Long id, @RequestParam(name = "name", defaultValue = "") String medicineName, @PageableDefault Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<MedicineStock> stockPage = medicineStockService.getMedicineStocksForPharmacy(pharmacy, medicineName.trim().toLowerCase(), pageable);
        return stockPage.map(toMedicineStockDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PostMapping("/{id}/stocks")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicineStockDTO registerMedicine(@PathVariable("id") Long id, @Valid @RequestBody MedicineStockRegistrationDTO medicineStockRegistrationDTO) {
        var medicine = medicineService.getByCode(medicineStockRegistrationDTO.getMedicineCode());
        var medicineStock = medicineStockService.registerMedicineInPharmacy(id, medicine, medicineStockRegistrationDTO.getPrice(), medicineStockRegistrationDTO.getQuantity());
        return toMedicineStockDTO.convert(medicineStock);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping("/{id}/stocks/all")
    public Collection<MedicineReducedDTO> getAllMedicines(@PathVariable("id") Long id) {
        var pharmacy = getOr404(id);
        return toMedicineReducedDTO.convert(medicineStockService.getPharmacyStockList(pharmacy));
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PutMapping("/{id}/stocks/{stockId}")
    public MedicineStockDTO updateMedicineStock(@PathVariable("id") Long id, @PathVariable("stockId") Long stockId, @Valid @RequestBody UpdateMedicineStockDTO updateMedicineStockDTO) {
        var medicineStock = medicineStockService.updateStock(id, stockId, updateMedicineStockDTO.getNewPrice());
        return toMedicineStockDTO.convert(medicineStock);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @DeleteMapping("/{id}/stocks/{stockId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeMedicine(@PathVariable("id") Long id, @PathVariable("stockId") Long stockId) {
        var pharmacy = getOr404(id);
        var medicineStock = medicineStockService.getStockInPharmacy(pharmacy.getId(), stockId);
        medicineStockService.removeMedicine(pharmacy, medicineStock);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping("/{id}/is-registered/{medicineId}")
    public Boolean isMedicineRegisteredInPharmacy(@PathVariable("id") Long id, @PathVariable("medicineId") Long medicineId) {
        var pharmacy = getOr404(id);
        return medicineStockService.isMedicineRegisteredInPharmacy(pharmacy, medicineService.get(medicineId));
    }
}
