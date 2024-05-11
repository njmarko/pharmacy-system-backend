package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.MedicineStock;
import com.mrsisa.pharmacy.domain.valueobjects.PromotionItem;
import com.mrsisa.pharmacy.dto.stock.MedicineStockDTO;
import com.mrsisa.pharmacy.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MedicineStockToMedicineStockDTO extends AbstractConverter<MedicineStock, MedicineStockDTO> implements IConverter<MedicineStock, MedicineStockDTO> {
    private final IPromotionService promotionService;

    @Autowired
    public MedicineStockToMedicineStockDTO(IPromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @Override
    public MedicineStockDTO convert(@NonNull  MedicineStock medicineStock) {
        MedicineStockDTO dto = getModelMapper().map(medicineStock, MedicineStockDTO.class);
        dto.setTotalDiscount(calculateTotalDiscount(medicineStock));
        return dto;
    }

    private int calculateTotalDiscount(MedicineStock medicineStock) {
        List<PromotionItem> promotionItems = promotionService.getAllActiveItemsForMedicineStock(medicineStock);
        double totalReductions = promotionItems.stream().reduce(0d, (total, item) -> total + item.getPriceReduction(), Double::sum);
        return 100 - (int) ((medicineStock.getCurrentPrice() / (medicineStock.getCurrentPrice() + totalReductions)) * 100);
    }
}
