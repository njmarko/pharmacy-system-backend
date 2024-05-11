package com.mrsisa.pharmacy.dto.recipe;

import com.mrsisa.pharmacy.dto.medicine.MedicineQRCodeReservationItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreationDTO {

    Long pharmacyId;
    List<MedicineQRCodeReservationItemDTO> stocks;

    public RecipeCreationDTO(Long pharmacyId){
        this.pharmacyId = pharmacyId;
        this.stocks = new ArrayList<>();
    }
}
