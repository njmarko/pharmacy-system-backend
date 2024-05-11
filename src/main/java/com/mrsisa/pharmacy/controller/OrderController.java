package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.dto.offer.OfferCreationDTO;
import com.mrsisa.pharmacy.dto.offer.OfferDTO;
import com.mrsisa.pharmacy.dto.order.OrderDetailsDTO;
import com.mrsisa.pharmacy.dto.order.OrderReducedInfoDTO;
import com.mrsisa.pharmacy.service.IOrderService;
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
@RequestMapping(value= "/api/orders")
public class OrderController {

    private final IOrderService orderService;
    private final IConverter<Order, OrderReducedInfoDTO> toOrderReducedInfoDTO;
    private final IConverter<Order, OrderDetailsDTO> toOrderDetailsDTO;
    private final IConverter<Offer, OfferDTO> toOfferDTO;

    @Autowired
    public OrderController(IOrderService orderService, IConverter<Order, OrderReducedInfoDTO> toOrderReducedInfoDTO, IConverter<Order, OrderDetailsDTO> toOrderDetailsDTO, IConverter<Offer, OfferDTO> toOfferDTO){
        this.orderService = orderService;
        this.toOrderReducedInfoDTO = toOrderReducedInfoDTO;
        this.toOrderDetailsDTO = toOrderDetailsDTO;
        this.toOfferDTO = toOfferDTO;
    }


    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    @GetMapping
    public Page<OrderReducedInfoDTO> getOrders(@PageableDefault Pageable page) {
        return this.orderService.getOrders(page).map(toOrderReducedInfoDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    @PostMapping("/{id}/offers")
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDTO createOffer(@PathVariable("id") Long orderId, @Valid @RequestBody OfferCreationDTO dto){
        var offer = this.orderService.createOffer(orderId, dto.getSupplierId(), dto.getDeliveryDate(), dto.getTotalPrice(), true);
        return toOfferDTO.convert(offer);
    }

    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    @GetMapping("/{id}")
    public OrderDetailsDTO getOrderDetails(@PathVariable("id") Long id){
        var order = this.orderService.getOrder(id);
        return toOrderDetailsDTO.convert(order);
    }
}
