package com.mrsisa.pharmacy.controller;

import com.mrsisa.pharmacy.aspect.OwnsEntity;
import com.mrsisa.pharmacy.aspect.OwnsPharmacy;
import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.domain.entities.PharmacyAdmin;
import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import com.mrsisa.pharmacy.dto.*;
import com.mrsisa.pharmacy.dto.medicine.MedicineOrderInfoDTO;
import com.mrsisa.pharmacy.dto.offer.OfferDTO;
import com.mrsisa.pharmacy.dto.order.OrderCreationDTO;
import com.mrsisa.pharmacy.dto.order.OrderDTO;
import com.mrsisa.pharmacy.dto.order.OrderReducedInfoDTO;
import com.mrsisa.pharmacy.dto.order.OrderUpdateDTO;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.service.*;
import com.mrsisa.pharmacy.support.IConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("api/pharmacies")
public class PharmacyOrderController extends PharmacyControllerBase {

    private final IOrderService orderService;
    private final IOfferService offerService;
    private final IUserService userService;
    private final IConverter<Order, OrderReducedInfoDTO> toOrderReducedDTO;
    private final IConverter<Order, OrderDTO> toOrderDTO;
    private final IConverter<Offer, OfferDTO> toOfferDTO;
    private final IConverter<MedicineOrderInfo, MedicineOrderInfoDTO> toItemDTO;

    @Autowired
    public PharmacyOrderController(IPharmacyService pharmacyService, IPharmacyAdminService pharmacyAdminService, IOrderService orderService, IOfferService offerService, IUserService userService, IConverter<Order, OrderReducedInfoDTO> toOrderReducedDTO, IConverter<Order, OrderDTO> toOrderDTO, IConverter<Offer, OfferDTO> toOfferDTO, IConverter<MedicineOrderInfo, MedicineOrderInfoDTO> toItemDTO) {
        super(pharmacyService, pharmacyAdminService);
        this.orderService = orderService;
        this.offerService = offerService;
        this.userService = userService;
        this.toOrderReducedDTO = toOrderReducedDTO;
        this.toOrderDTO = toOrderDTO;
        this.toOfferDTO = toOfferDTO;
        this.toItemDTO = toItemDTO;
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/orders")
    public Page<OrderReducedInfoDTO> getPharmacyOrders(@PathVariable("id") Long id, @RequestParam(name = "status", required = false) OrderStatus orderStatus, @PageableDefault Pageable pageable) {
        var pharmacy = getOr404(id);
        Page<Order> orderPage = orderService.getOrderForPharmacy(pharmacy, orderStatus, pageable);
        return orderPage.map(toOrderReducedDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/orders/{orderId}")
    public OrderDTO getOrderDetails(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId) {
        var order = orderService.getOrder(orderId);
        return toOrderDTO.convert(order);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @OwnsEntity(entityId = "orderId", ownerField = "pharmacyAdmin", entity = Order.class)
    @PutMapping(value = "/{id}/orders/{orderId}")
    public OrderDTO updateOrder(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId, @Valid @RequestBody OrderUpdateDTO orderUpdateDTO) {
        var order = orderService.updateOrder(orderId, orderUpdateDTO.getDueDate());
        return toOrderDTO.convert(order);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsEntity(entityId = "orderId", ownerField = "pharmacyAdmin", entity = Order.class)
    @DeleteMapping(value = "/{id}/orders/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId) {
        orderService.delete(orderId);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/orders/{orderId}/offers")
    public Page<OfferDTO> getOffersForOrder(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId, @RequestParam(value = "name", defaultValue = "") String query, @PageableDefault Pageable pageable) {
        var order = orderService.get(orderId);
        if (!order.getPharmacyAdmin().getPharmacy().getId().equals(id)) {
            throw new NotFoundException("Cannot find entity with id: " + orderId);
        }
        Page<Offer> offerPage = offerService.getOffersForOrder(order, query, pageable);
        return offerPage.map(toOfferDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @GetMapping(value = "/{id}/orders/{orderId}/items")
    public Page<MedicineOrderInfoDTO> getOrderItems(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId, @RequestParam(name = "name", defaultValue = "") String name, @PageableDefault Pageable pageable) {
        Page<MedicineOrderInfo> itemPage = orderService.getOrderItems(getOr404(id), orderId, name, pageable);
        return itemPage.map(toItemDTO::convert);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @OwnsEntity(entityId = "orderId", ownerField = "pharmacyAdmin", entity = Order.class)
    @PostMapping(value = "/{id}/orders/{orderId}/offers/{offerId}/accept")
    public OfferDTO acceptOffer(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId, @PathVariable("offerId") Long offerId) {
        orderService.acceptOffer(orderId, offerId);
        return toOfferDTO.convert(offerService.get(offerId));
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @PostMapping(value = "/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@PathVariable("id") Long id, Principal principal, @Valid @RequestBody OrderCreationDTO orderCreationDTO) {
        var pharmacy = getOr404(id);
        var pharmacyAdmin = (PharmacyAdmin) userService.findByUsernameWithAuthorities(principal.getName());
        var createdOrder = orderService.save(new Order(orderCreationDTO.getDueDate(), OrderStatus.IN_CREATION, pharmacyAdmin, pharmacy));
        return toOrderDTO.convert(createdOrder);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @OwnsEntity(entityId = "orderId", ownerField = "pharmacyAdmin", entity = Order.class)
    @PutMapping(value = "/{id}/orders/{orderId}/publish")
    public OrderDTO publishOrder(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId) {
        var order = orderService.publish(orderId);
        return toOrderDTO.convert(order);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @OwnsEntity(entityId = "orderId", ownerField = "pharmacyAdmin", entity = Order.class)
    @PostMapping(value = "/{id}/orders/{orderId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO addOrderItem(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId, @Valid @RequestBody AddOrderItemDTO addOrderItemDTO) {
        var order = orderService.addOrderItem(orderId, addOrderItemDTO.getMedicineId(), addOrderItemDTO.getQuantity(), addOrderItemDTO.getIsNew(), addOrderItemDTO.getNewPrice());
        return toOrderDTO.convert(order);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @OwnsEntity(entityId = "orderId", ownerField = "pharmacyAdmin", entity = Order.class)
    @PutMapping(value = "/{id}/orders/{orderId}/items/{itemId}")
    public OrderDTO updateOrderItem(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId, @PathVariable("itemId") Long itemId, @Valid @RequestBody UpdateOrderItemDTO updateOrderItemDTO) {
        var order = orderService.updateOrderItem(orderId, itemId, updateOrderItemDTO.getQuantity());
        return toOrderDTO.convert(order);
    }

    @PreAuthorize("hasRole('ROLE_PHARMACY_ADMIN')")
    @OwnsPharmacy(identifier = "id")
    @OwnsEntity(entityId = "orderId", ownerField = "pharmacyAdmin", entity = Order.class)
    @DeleteMapping(value = "/{id}/orders/{orderId}/items/{itemId}")
    public OrderDTO removeOrderItem(@PathVariable("id") Long id, @PathVariable("orderId") Long orderId, @PathVariable("itemId") Long itemId) {
        var order = orderService.removeOrderItem(orderId, itemId);
        return toOrderDTO.convert(order);
    }
}
