package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwningUser;
import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.dto.*;
import com.mrsisa.pharmacy.dto.offer.OfferDTO;
import com.mrsisa.pharmacy.dto.offer.OfferSearchDTO;
import com.mrsisa.pharmacy.dto.offer.OfferUpdateDTO;
import com.mrsisa.pharmacy.dto.supplier.SupplierDTO;
import com.mrsisa.pharmacy.dto.supplier.SupplierRegistrationDTO;
import com.mrsisa.pharmacy.dto.supplier.SupplierUpdateDTO;
import com.mrsisa.pharmacy.service.IOfferService;
import com.mrsisa.pharmacy.service.ISupplierService;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value= "/api/suppliers")
public class SupplierController {

    private final ISupplierService supplierService;
    private final IOfferService offerService;
    private final IConverter<Offer, OfferDTO> toOfferDTO;

    @Autowired
    public SupplierController(ISupplierService supplierService, IOfferService offerService, IConverter<Offer, OfferDTO> toOfferDTO){
        this.supplierService = supplierService;
        this.offerService = offerService;
        this.toOfferDTO = toOfferDTO;
    }


    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerSupplier(@Valid @RequestBody SupplierRegistrationDTO dto){
        var s = this.supplierService.registerSupplier(dto.getFirstName(), dto.getLastName(), dto.getUsername(), dto.getPassword(), dto.getEmail(),  dto.getCompany());
        return new UserDTO(s.getUsername(), s.getEmail(), s.getFirstName(), s.getLastName(), s.getId(), s.getVerified());
    }


    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    @GetMapping("/{id}/offers")
    @OwningUser
    public Page<OfferDTO> getOffers(@PathVariable("id") Long id, @Valid OfferSearchDTO dto, @PageableDefault Pageable pageable){
       return this.offerService.getOffersForSupplier(id, dto.getStatus(), pageable).map(toOfferDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    @PutMapping("/{supplierId}/offers/{offerId}")
    @OwningUser(identifier = "supplierId")
    public OfferDTO updateOffer(@PathVariable("supplierId") Long supplierId, @PathVariable("offerId") Long offerId, @RequestBody OfferUpdateDTO dto){
        var offer = this.offerService.updateOffer(supplierId, offerId, dto.getDeliveryDate(), dto.getTotalCost());
        return toOfferDTO.convert(offer);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    @OwningUser
    public SupplierDTO updateProfileInfo(@PathVariable("id") Long id, @RequestBody @Valid SupplierUpdateDTO dto) {
        var supplier = this.supplierService.updateProfileInfo(id, dto.getFirstName(), dto.getLastName(), dto.getCompany());
        return new SupplierDTO(supplier.getFirstName(), supplier.getLastName(), supplier.getUsername(), supplier.getEmail(), supplier.getCompany());

    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    @OwningUser
    public SupplierDTO getSupplier(@PathVariable("id") Long id){
        var supplier = this.supplierService.getSupplier(id);
        return new SupplierDTO(supplier.getFirstName(), supplier.getLastName(), supplier.getUsername(), supplier.getEmail(), supplier.getCompany());

    }

}