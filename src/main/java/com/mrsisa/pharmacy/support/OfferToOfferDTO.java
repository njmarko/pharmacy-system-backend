package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.dto.offer.OfferDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class OfferToOfferDTO extends AbstractConverter<Offer, OfferDTO>{
    @Override
    public OfferDTO convert(@NonNull Offer offer) {
        return getModelMapper().map(offer, OfferDTO.class);
    }
}
