package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.dto.medicine.MedicinePrescriptionSearchDTO;
import com.mrsisa.pharmacy.dto.medicine.ReplacementMedicineDTO;
import com.mrsisa.pharmacy.dto.stock.MedicineStockDetailedDTO;
import com.mrsisa.pharmacy.dto.stock.MedicineStockReducedInfoDTO;
import com.mrsisa.pharmacy.service.IEmploymentContractService;
import com.mrsisa.pharmacy.service.IMedicineStockService;
import com.mrsisa.pharmacy.service.IMissingMedicineLogService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(value= "/api/medicine-stocks")
public class MedicineStockController {

    private final IMedicineStockService medicineStockService;
    private final IEmploymentContractService employmentContractService;
    private final IMissingMedicineLogService missingMedicineLogService;
    private final IConverter<MedicineStock, MedicineStockReducedInfoDTO> converter;
    private final IConverter<MedicineStock, MedicineStockDetailedDTO> detailedDTOIConverter;

    @Autowired
    public MedicineStockController(IMedicineStockService medicineStockService,
                                   IConverter<MedicineStock, MedicineStockReducedInfoDTO> converter,
                                   IEmploymentContractService employmentContractService,
                                   IConverter<MedicineStock, MedicineStockDetailedDTO> detailedDTOIConverter,
                                   IMissingMedicineLogService missingMedicineLogService){
        this.medicineStockService = medicineStockService;
        this.converter = converter;
        this.employmentContractService = employmentContractService;
        this.detailedDTOIConverter = detailedDTOIConverter;
        this.missingMedicineLogService = missingMedicineLogService;
    }


    @GetMapping("/medicine/{id}")
    public Page<MedicineStockReducedInfoDTO> getPharmaciesWhereMedicineIsAvailable(@PathVariable("id") Long id, @PageableDefault Pageable pageable){
        Page<MedicineStock> stockPage = this.medicineStockService.getAvailableMedicineStocksForMedicine(id, pageable);
        return stockPage.map(converter::convert);
    }


    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @GetMapping("/all-prescriptions/{id}")
    @OwningUser
    public Page<MedicineStockDetailedDTO> getAllMedicinesForPrescription(@PathVariable("id") Long id,
                                                                   MedicinePrescriptionSearchDTO medicinePrescriptionSearchDTO,
                                                                   @PageableDefault Pageable pageable) {
        checkIfEmployeeWorksInPharmacy(id, medicinePrescriptionSearchDTO.getPharmacyId());

        Page<MedicineStock> stockPage = medicineStockService.getAllMedicinesForPharmacyAndPatientIsNotAllergicTo(medicinePrescriptionSearchDTO.getPharmacyId(),
                medicinePrescriptionSearchDTO.getPatientId(), medicinePrescriptionSearchDTO.getMedicineName().toLowerCase(),
                medicinePrescriptionSearchDTO.getChosenMedicineIds(), pageable);

        return stockPage.map(detailedDTOIConverter::convert);
    }

    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @GetMapping("/medicine-in-stock/{id}")
    @OwningUser
    public boolean checkIfMedicineInStock(@PathVariable("id") Long id, ReplacementMedicineDTO replacementMedicineDTO) {
        checkIfEmployeeWorksInPharmacy(id, replacementMedicineDTO.getPharmacyId());
        var medicineStock = medicineStockService.get(replacementMedicineDTO.getMedicineStockId());
        if (medicineStock.getQuantity() > 0) {
            return true;
        }

        missingMedicineLogService.insertMissingMedicineLog(replacementMedicineDTO.getAppointmentId(), replacementMedicineDTO.getMedicineStockId());
        return false;
    }

    @PreAuthorize("hasAnyRole('ROLE_PHARMACIST', 'ROLE_DERMATOLOGIST')")
    @GetMapping("/all-replacement-medicines/{id}")
    @OwningUser
    public Page<MedicineStockDetailedDTO> getReplacementsForMedicine(@PathVariable("id") Long id,
                                                                   ReplacementMedicineDTO replacementMedicineDTO,
                                                                   @PageableDefault Pageable pageable) {
        checkIfEmployeeWorksInPharmacy(id, replacementMedicineDTO.getPharmacyId());

        Page<MedicineStock> stockPage = medicineStockService.getReplacementMedicinesPatientIsNotAllergicTo(replacementMedicineDTO.getPharmacyId(),
                replacementMedicineDTO.getPatientId(), replacementMedicineDTO.getMedicineStockId(),
                replacementMedicineDTO.getMedicineName().toLowerCase(),
                replacementMedicineDTO.getChosenMedicineIds(), pageable);

        return stockPage.map(detailedDTOIConverter::convert);
    }

    private void checkIfEmployeeWorksInPharmacy(Long employeeId, Long pharmacyId) {
        if (!employmentContractService.contractWithPharmacyExists(employeeId, pharmacyId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't see medicines for requested pharmacy!");
        }
    }


}