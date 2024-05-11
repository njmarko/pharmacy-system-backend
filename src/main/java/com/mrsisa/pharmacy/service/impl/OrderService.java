package com.mrsisa.pharmacy.service.impl;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.OfferStatus;
import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import com.mrsisa.pharmacy.exception.BusinessException;
import com.mrsisa.pharmacy.exception.NotFoundException;
import com.mrsisa.pharmacy.repository.*;
import com.mrsisa.pharmacy.service.IEmailService;
import com.mrsisa.pharmacy.service.IMedicineStockService;
import com.mrsisa.pharmacy.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class OrderService extends JPAService<Order> implements IOrderService {
    private final IMedicineStockService medicineStockService;
    private final IOrderRepository orderRepository;
    private final IMedicineRepository medicineRepository;
    private final ISupplierRepository supplierRepository;
    private final IOfferRepository offerRepository;
    private final IMedicineStockRepository medicineStockRepository;
    private final IOrderItemRepository orderItemRepository;
    private final IEmailService emailService;

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(IMedicineStockService medicineStockService, IOrderRepository orderRepository, IMedicineRepository medicineRepository, ISupplierRepository supplierRepository, IOfferRepository offerRepository, IMedicineStockRepository medicineStockRepository, IOrderItemRepository orderItemRepository, IEmailService emailService) {
        this.medicineStockService = medicineStockService;
        this.orderRepository = orderRepository;
        this.medicineRepository = medicineRepository;
        this.supplierRepository = supplierRepository;
        this.offerRepository = offerRepository;
        this.medicineStockRepository = medicineStockRepository;
        this.orderItemRepository = orderItemRepository;
        this.emailService = emailService;
    }

    @Override
    protected JpaRepository<Order, Long> getEntityRepository() {
        return orderRepository;
    }

    @Override
    public Page<Order> getOrders(Pageable pageable) {
        return this.orderRepository.getOrders(LocalDateTime.now(), OrderStatus.WAITING_FOR_OFFERS, pageable);
    }

    @Override
    public Offer createOffer(Long orderId, Long supplierId, LocalDateTime deliveryDate, Double totalCost, boolean multipleOffersForOrder) {
        Optional<Order> order = this.orderRepository.getOrderByActiveTrueAndDueDateAfterAndId(LocalDateTime.now(), orderId);
        if(order.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order does not exist or has been finished.");

        if (order.get().getOrderStatus() == OrderStatus.IN_CREATION)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find order with id: " + orderId);
        if(order.get().getOrderStatus() == OrderStatus.PROCESSED)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This order has already been processed.");

        Optional<Supplier> supplier = this.supplierRepository.getSupplierByActiveTrueAndId(supplierId);
        if(supplier.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier does not exist.");

        if(!multipleOffersForOrder){
            Optional<Offer> existingOffer = this.offerRepository.getOfferForSupplierAndOrder(supplierId, orderId);

            if(existingOffer.isPresent())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already made an offer for this order.");
        }

        if(deliveryDate.isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery date cannot be in the past.");
        if(totalCost < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total cost cannot be lower than 0.");

        if(order.get().getDueDate().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deadline for offers for this order has passed.");

        var offer = new Offer(totalCost, deliveryDate, OfferStatus.PENDING, supplier.get(), order.get());
        this.offerRepository.save(offer);
        return offer;
    }

    @Override
    public Order getOrder(Long id) {
        Optional<Order> orderOptional = this.orderRepository.getOrderDetails(id);
        if(orderOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order does not exist.");
        return orderOptional.get();
    }

    @Override
    public Page<Order> getOrderForPharmacy(Pharmacy pharmacy, OrderStatus orderStatus, Pageable pageable) {
        return orderRepository.findOrdersForPharmacy(pharmacy.getId(), orderStatus, pageable);
    }

    @Override
    public void acceptOffer(Long orderId, Long offerId) {
        log.info("Starting accept offer...");
        var order = get(orderId);
        if (order.getOrderStatus() == OrderStatus.PROCESSED) {
            throw new BusinessException("Order with id " + orderId + " has already been processed.");
        }
        if (order.getOrderStatus() == OrderStatus.IN_CREATION) {
            throw new BusinessException("Order is not published yet.");
        }
        if (order.getDueDate().isAfter(LocalDateTime.now())) {
            throw new BusinessException("There is still time for suppliers to make their offers.");
        }
        // Update stock
        medicineStockService.updatePharmacyStock(order.getPharmacy().getId(), orderId);
        // Update offer item status
        var acceptedOffer = offerRepository.findOfferForOrder(offerId, orderId).orElseThrow(() -> new NotFoundException("Cannot find offer with id: " + offerId));
        acceptedOffer.setOfferStatus(OfferStatus.ACCEPTED);
        order.getAvailableOffers().forEach(offer -> {
            if (!offer.getId().equals(acceptedOffer.getId())) {
                offer.setOfferStatus(OfferStatus.REJECTED);
            }
            emailService.notifySupplier(offer);
        });
        order.setOrderStatus(OrderStatus.PROCESSED);
        log.info("Finished accept offer...");
    }

    @Override
    public Order addOrderItem(Long orderId, Long medicineId, Integer quantity, Boolean isNew, Double newPrice) {
        var order = getValidOrderForUpdate(orderId);
        var medicine = medicineRepository.findByIdAndActiveTrue(medicineId);
        if (medicine == null) {
            throw new NotFoundException("Cannot find medicine with id: " + medicineId);
        }
        Optional<MedicineStock> stock = medicineStockRepository.getMedicineInPharmacy(order.getPharmacy().getId(), medicineId);
        // Check if the medicine is registered in this pharmacy
        if (Boolean.TRUE.equals(isNew)) {
            if (stock.isPresent()) {
                throw new BusinessException("Medicine is already registered in this pharmacy.");
            }
        } else {
            if (stock.isEmpty()) {
                throw new BusinessException("Medicine is not registered in this pharmacy.");
            }
        }
        // Check if the same medicine has already been added to this order
        orderItemRepository.getItemWithMedicine(orderId, medicineId).ifPresent(item -> {
            throw new BusinessException("Medicine is already added to this order.");
        });
        var item = new MedicineOrderInfo(order, quantity, medicine, isNew, newPrice);
        order.getOrderItems().add(item);
        return order;
    }

    @Override
    public Order removeOrderItem(Long orderId, Long itemId) {
        var order = getValidOrderForUpdate(orderId);
        if (orderItemRepository.getItemsForOrderStream(orderId).count() == 1 && order.getOrderStatus() == OrderStatus.WAITING_FOR_OFFERS) {
            throw new BusinessException("Cannot leave the order empty. Add something and then try to remove items.");
        }
        var item = orderItemRepository.findItemForOrder(order.getId(), itemId).orElseThrow(() -> new NotFoundException("Cannot find item with id: " + itemId));
        item.setActive(false);
        order.getOrderItems().remove(item);
        return order;
    }

    @Override
    public Page<MedicineOrderInfo> getOrderItems(Pharmacy pharmacy, Long orderId, String name, Pageable pageable) {
        var order = get(orderId);
        if (!order.getPharmacy().getId().equals(pharmacy.getId())) {
            throw new BusinessException("Order does not belong to this pharmacy.");
        }
        return orderItemRepository.getItemsForOrder(orderId, name, pageable);
    }

    @Override
    public Order publish(Long orderId) {
        var order = get(orderId);
        if (order.getOrderStatus() != OrderStatus.IN_CREATION) {
            throw new BusinessException("Order is already published.");
        }
        orderItemRepository.getItemsForOrderStream(orderId).findAny().orElseThrow(() -> new BusinessException("Cannot publish empty order."));
        order.setOrderStatus(OrderStatus.WAITING_FOR_OFFERS);
        return order;
    }

    @Override
    public Order updateOrder(Long orderId, LocalDateTime dueDate) {
        var order = getValidOrderForUpdate(orderId);
        order.setDueDate(dueDate);
        return order;
    }

    @Override
    public Order updateOrderItem(Long orderId, Long itemId, Integer quantity) {
        var order = getValidOrderForUpdate(orderId);
        var item = orderItemRepository.findItemForOrder(order.getId(), itemId).orElseThrow(() -> new NotFoundException("Cannot find order item with id: " + itemId));
        item.setQuantity(quantity);
        return order;
    }

    @Override
    public Long getOfferCount(Order order) {
        return offerRepository.getOfferCountForOrder(order.getId()).orElse(0L);
    }

    @Override
    public void delete(Long id) {
        var order = get(id);
        if (order.getOrderStatus() == OrderStatus.PROCESSED) {
            throw new BusinessException("Cannot delete processed order.");
        }
        offerRepository.getOrderOffersStream(id).findAny().ifPresent(offer -> {
            throw new BusinessException("Cannot delete order because at least one supplier made an offer.");
        });
        order.getOrderItems().forEach(item -> item.setActive(false));
        super.delete(id);
    }

    private Order getValidOrderForUpdate(Long orderId) {
        var order = get(orderId);
        if (order.getOrderStatus() == OrderStatus.PROCESSED) {
            throw new BusinessException("Cannot modify processed order.");
        }
        offerRepository.getOrderOffersStream(orderId).findAny().ifPresent(offer -> {
            throw new BusinessException("Suppliers already made their offers to this order.");
        });
        return order;
    }
}
