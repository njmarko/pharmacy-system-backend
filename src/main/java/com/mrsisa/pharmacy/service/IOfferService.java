package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.domain.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IOfferService extends IJPAService<Offer> {
    Page<Offer> getOffersForSupplier(Long supplierId, OfferStatus status, Pageable pageable);
    Offer updateOffer(Long supplierId, Long offerId, LocalDateTime deliveryDate, Double totalCost);
    Page<Offer> getOffersForOrder(Order order, String query, Pageable pageable);
}
