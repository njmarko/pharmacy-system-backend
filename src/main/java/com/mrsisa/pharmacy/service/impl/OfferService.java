package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.domain.entities.Supplier;
import com.mrsisa.pharmacy.domain.enums.OfferStatus;
import com.mrsisa.pharmacy.repository.IOfferRepository;
import com.mrsisa.pharmacy.repository.ISupplierRepository;
import com.mrsisa.pharmacy.service.IOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OfferService extends JPAService<Offer> implements IOfferService {
    private final IOfferRepository offerRepository;
    private final ISupplierRepository supplierRepository;

    @Autowired
    public OfferService(IOfferRepository offerRepository, ISupplierRepository supplierRepository) {
        this.offerRepository = offerRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    protected JpaRepository<Offer, Long> getEntityRepository() {
        return offerRepository;
    }

    @Override
    public Page<Offer> getOffersForSupplier(Long supplierId, OfferStatus status, Pageable pageable) {
        return this.offerRepository.getOfferForSupplierAndStatus(supplierId, status, pageable);
    }

    @Override
    public Offer updateOffer(Long supplierId, Long offerId, LocalDateTime deliveryDate, Double totalCost) {
        Optional<Offer> optionalOffer = this.offerRepository.getOfferForSupplierUsingOfferId(supplierId, offerId);
        if(optionalOffer.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offer does not exist.");
        var offer = optionalOffer.get();
        if(offer.getOriginalOrder().getDueDate().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deadline for offers for this order has passed.");
        if(offer.getOfferStatus() != OfferStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot update an offer that isn't pending.");
        if(totalCost < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total cost cannot be lower than 0.");
        if(deliveryDate.isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery date cannot be in the past.");

        Optional<Supplier> supplier = this.supplierRepository.getSupplierByActiveTrueAndId(supplierId);
        if(supplier.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier does not exist.");

        offer.setDeliveryDueDate(deliveryDate);
        offer.setTotalPrice(totalCost);
        this.update(offer);
        return offer;
    }

    @Override
    public Page<Offer> getOffersForOrder(Order order, String query, Pageable pageable) {
        return offerRepository.getOffersForOrder(order.getId(), query, pageable);
    }
}
