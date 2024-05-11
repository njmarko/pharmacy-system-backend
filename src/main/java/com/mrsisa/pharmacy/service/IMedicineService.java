package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.enums.MedicineShape;
import com.mrsisa.pharmacy.domain.enums.MedicineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMedicineService extends IJPAService<Medicine> {

    Page<Medicine> getAllMedicineSearchAndFilter(String name, Double lowGrade, Double highGrade, Boolean issueOnRecipe, MedicineType type,  Pageable pageable);

    Medicine getMedicine(Long id);

    Medicine registerMedicine(String code, String name, MedicineShape shape, MedicineType type, String composition, String manufacturer, Boolean issueOnRecipe, String notes, Integer points, List<Long> replacementIds);

    List<Medicine> getAllMedicine();

    Medicine getByCode(String medicineCode);

    Page<Medicine> getAllNotInOrder(Long orderId, String name, Double lowGrade, Double highGrade, Boolean issueOnRecipe, MedicineType medicineType, Pageable pageable);

    void rateDrug(Long patientId, Long drugId, Integer rating);
}
