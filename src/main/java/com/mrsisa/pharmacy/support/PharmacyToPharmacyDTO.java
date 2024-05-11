package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.dto.pharmacy.PharmacyDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PharmacyToPharmacyDTO extends AbstractConverter<Pharmacy, PharmacyDTO> {

    @Override
    public PharmacyDTO convert(@NonNull Pharmacy pharmacy) {
        return getModelMapper().map(pharmacy, PharmacyDTO.class);
    }

}
