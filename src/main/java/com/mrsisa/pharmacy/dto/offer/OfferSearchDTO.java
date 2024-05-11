package com.mrsisa.pharmacy.dto.offer;


import com.mrsisa.pharmacy.domain.enums.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferSearchDTO {
    private OfferStatus status;
}
