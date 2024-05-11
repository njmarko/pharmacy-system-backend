package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.domain.entities.Order;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.OfferStatus;
import com.mrsisa.pharmacy.domain.enums.OrderStatus;
import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import com.mrsisa.pharmacy.repository.IOfferRepository;
import com.mrsisa.pharmacy.repository.IOrderItemRepository;
import com.mrsisa.pharmacy.service.impl.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Mock
    private IOfferRepository offerRepositoryMock;

    @Mock
    private IOrderItemRepository orderItemRepositoryMock;

    @Mock
    private IEmailService emailServiceMock;

    @Mock
    private IMedicineStockService medicineStockServiceMock;

    @InjectMocks
    private OrderService orderService;

    @Test
    @Transactional
    void testPublishOrder() {
        // Create dummy data
        Order order = new Order();
        order.setId(789L);
        order.setOrderStatus(OrderStatus.IN_CREATION);
        order.getOrderItems().add(new MedicineOrderInfo());

        // Create spy object because OrderService::publishOrder internally calls OrderService::get
        OrderService orderServiceSpy = spy(orderService);
        doReturn(order).when(orderServiceSpy).get(order.getId());

        // Pass if the order has at least one items in it
        order.setOrderStatus(OrderStatus.IN_CREATION);
        order.getOrderItems().add(new MedicineOrderInfo());
        // Mock order item repository to return that the order has added items
        when(orderItemRepositoryMock.getItemsForOrderStream(order.getId())).thenReturn(order.getOrderItems().stream());
        Order publishedOrder = orderServiceSpy.publish(order.getId());
        assertEquals(OrderStatus.WAITING_FOR_OFFERS, publishedOrder.getOrderStatus());
        verify(orderItemRepositoryMock, times(1)).getItemsForOrderStream(order.getId());
    }

    @Test
    @Transactional
    void testAcceptOffer() {
        // Test cast constants
        final Long PHARMACY_ID = 1234L;
        final Long ORDER_ID = 789L;
        final Long ACCEPTED_OFFER_ID = 123L;
        final Long REJECTED_OFFER_ID = 456L;
        final int NUM_OFFERS = 2;

        // Creating dummy data
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(PHARMACY_ID);
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setOrderStatus(OrderStatus.WAITING_FOR_OFFERS);
        order.setDueDate(LocalDateTime.now().minusDays(1));
        order.getOrderItems().add(new MedicineOrderInfo());
        order.setPharmacy(pharmacy);
        Offer offerToAccept = new Offer(100.0, LocalDateTime.now(), OfferStatus.PENDING, null, order);
        offerToAccept.setId(ACCEPTED_OFFER_ID);
        Offer offerToReject = new Offer(100.0, LocalDateTime.now(), OfferStatus.PENDING, null, order);
        offerToReject.setId(REJECTED_OFFER_ID);
        order.getAvailableOffers().add(offerToAccept);
        order.getAvailableOffers().add(offerToReject);

        // Mock offer repository to return offer which should be accepted
        when(offerRepositoryMock.findOfferForOrder(ACCEPTED_OFFER_ID, ORDER_ID)).thenReturn(Optional.of(offerToAccept));

        // Mock email service to do nothing when it should send notification to the supplier
        doNothing().when(emailServiceMock).notifySupplier(any(Offer.class));

        // Mock medicine stock service
        doNothing().when(medicineStockServiceMock).updatePharmacyStock(PHARMACY_ID, ORDER_ID);

        // Create spy object because OrderService::acceptOffer internally calls OrderService::get
        OrderService orderServiceSpy = spy(orderService);
        doReturn(order).when(orderServiceSpy).get(ORDER_ID);

        // Pass if everything is ok
        orderServiceSpy.acceptOffer(ORDER_ID, ACCEPTED_OFFER_ID);
        assertEquals(OrderStatus.PROCESSED, order.getOrderStatus());
        assertEquals(OfferStatus.ACCEPTED, offerToAccept.getOfferStatus());
        assertEquals(OfferStatus.REJECTED, offerToReject.getOfferStatus());
        verify(offerRepositoryMock, times(1)).findOfferForOrder(ACCEPTED_OFFER_ID, ORDER_ID);
        verify(emailServiceMock, times(NUM_OFFERS)).notifySupplier(any(Offer.class));
        verify(emailServiceMock, times(1)).notifySupplier(offerToAccept);
        verify(emailServiceMock, times(1)).notifySupplier(offerToReject);
    }

}
