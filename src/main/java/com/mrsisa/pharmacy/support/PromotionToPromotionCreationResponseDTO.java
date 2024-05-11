package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Promotion;
import com.mrsisa.pharmacy.dto.promotion.PromotionCreationResponseDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PromotionToPromotionCreationResponseDTO extends AbstractConverter<Promotion, PromotionCreationResponseDTO> {
    @Override
    public PromotionCreationResponseDTO convert(@NonNull Promotion promotion) {
        return getModelMapper().map(promotion, PromotionCreationResponseDTO.class);
    }
}
