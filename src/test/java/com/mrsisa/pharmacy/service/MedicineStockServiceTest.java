package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.PromotionStatus;
import com.mrsisa.pharmacy.repository.IPromotionItemRepository;
import com.mrsisa.pharmacy.repository.IStockPriceRepository;
import com.mrsisa.pharmacy.service.impl.MedicineStockService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class MedicineStockServiceTest {

    @Mock
    private IPromotionItemRepository promotionItemRepositoryMock;

    @Mock
    private IStockPriceRepository stockPriceRepositoryMock;

    @InjectMocks
    private MedicineStockService medicineStockService;

    @Test
    @Transactional
    void testUpdateStock() {
        // Test case constants
        final Long PHARMACY_ID = 123L;
        final Long STOCK_ID = 123L;
        final Long MEDICINE_ID = 123L;
        final Double OLD_PRICE = 250.0;
        final Double NEW_PRICE = 350.0;

        // Create dummy data
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(PHARMACY_ID);
        Medicine medicine = new Medicine();
        medicine.setId(MEDICINE_ID);
        MedicineStock medicineStock = new MedicineStock(100, pharmacy, medicine);
        StockPrice oldPrice = new StockPrice(OLD_PRICE, LocalDate.now().minusDays(7), null, false, medicineStock);
        medicineStock.setCurrentPrice(OLD_PRICE);
        medicineStock.getPriceTags().add(oldPrice);

        // Mock repositories
        when(promotionItemRepositoryMock.getItemsWithMedicineInPharmacyStream(eq(PHARMACY_ID), eq(MEDICINE_ID), eq(PromotionStatus.ACTIVE), any(LocalDate.class))).thenReturn(Stream.empty());
        when(stockPriceRepositoryMock.findActiveForStock(STOCK_ID)).thenReturn(Optional.of(oldPrice));

        // Create a spy object
        MedicineStockService medicineStockServiceSpy = spy(medicineStockService);
        doReturn(medicineStock).when(medicineStockServiceSpy).save(medicineStock);
        doReturn(medicineStock).when(medicineStockServiceSpy).getStockInPharmacy(PHARMACY_ID, STOCK_ID);

        // Verification
        MedicineStock updatedStock = medicineStockServiceSpy.updateStock(PHARMACY_ID, STOCK_ID, NEW_PRICE);
        assertEquals(updatedStock.getCurrentPrice(), NEW_PRICE);
        assertEquals(2, updatedStock.getPriceTags().size());
        assertNotNull(oldPrice.getTo());
        assertTrue(oldPrice.getTo().isEqual(LocalDate.now()));
        verify(promotionItemRepositoryMock, times(1)).getItemsWithMedicineInPharmacyStream(eq(PHARMACY_ID), eq(MEDICINE_ID), eq(PromotionStatus.ACTIVE), any(LocalDate.class));
        verify(stockPriceRepositoryMock, times(1)).findActiveForStock(STOCK_ID);
    }

}
