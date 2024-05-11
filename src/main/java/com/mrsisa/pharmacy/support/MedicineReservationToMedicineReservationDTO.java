package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.MedicineReservation;
import com.mrsisa.pharmacy.domain.entities.Review;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineReservationItem;
import com.mrsisa.pharmacy.dto.medicine.MedicineReservationDTO;
import com.mrsisa.pharmacy.dto.medicine.MedicineReservationItemDTO;
import com.mrsisa.pharmacy.service.IMedicineReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MedicineReservationToMedicineReservationDTO
        extends AbstractConverter<MedicineReservation, MedicineReservationDTO>
        implements IConverter<MedicineReservation, MedicineReservationDTO> {

    private final IMedicineReservationService medicineReservationService;

    @Autowired
    public MedicineReservationToMedicineReservationDTO(IMedicineReservationService medicineReservationService) {
        this.medicineReservationService = medicineReservationService;
    }

    @Override
    public MedicineReservationDTO convert(@NonNull MedicineReservation medicineReservation) {
        MedicineReservationDTO dto = getModelMapper().map(medicineReservation, MedicineReservationDTO.class);

        List<MedicineReservationItem> items = new ArrayList<>(medicineReservation.getReservedMedicines());
        dto.setItemsDTO
                (items.stream().map(item -> new MedicineReservationItemDTO(item.getQuantity(), item.getMedicine().getId(),
                        item.getMedicine().getName())).collect(Collectors.toList()));
        dto.getItemsDTO().sort(Comparator.comparing(MedicineReservationItemDTO::getMedicineId));
        // get current rating
        if (!items.isEmpty()) {
            dto.setRating(items.get(0).getMedicine().getAverageGrade());
            dto.setDrugId(items.get(0).getMedicine().getId());
            dto.setMedicineName(items.get(0).getMedicine().getName());
            dto.setQuantity(items.get(0).getQuantity());
        }
        // get users previous rating for this drug
        Review review = null;
        if (medicineReservation.getPatient() != null && !items.isEmpty()) {
            review = this.medicineReservationService.
                    getPatientReviewForDrug(medicineReservation.getPatient().getId(),
                            items.get(0).getMedicine().getId());
            if (review != null) {
                dto.setPreviousRating(review.getGrade());
            }
        }
        return dto;
    }
}
