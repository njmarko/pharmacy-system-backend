package com.mrsisa.pharmacy.validation.validator;

import com.mrsisa.pharmacy.dto.promotion.PromotionCreationDTO;
import com.mrsisa.pharmacy.dto.promotion.PromotionItemDTO;
import com.mrsisa.pharmacy.validation.constraint.PromotionConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class PromotionValidator implements ConstraintValidator<PromotionConstraint, PromotionCreationDTO> {
    @Override
    public void initialize(PromotionConstraint constraintAnnotation) {
        // Empty
    }

    @Override
    public boolean isValid(PromotionCreationDTO dto, ConstraintValidatorContext constraintValidatorContext) {
        // Validate content
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return false;
        }

        // Validate from date
        if (dto.getFrom() == null || dto.getFrom().isBefore(LocalDate.now())) {
            return false;
        }

        // Validate to date
        if (dto.getTo() == null || dto.getTo().isBefore(dto.getFrom())) {
            return false;
        }

        // Check if there are any items in the promotions
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            return false;
        }

        // Validate promotion items
        Set<Long> addedIds = new HashSet<>();
        for (PromotionItemDTO item: dto.getItems()) {
            if (addedIds.contains(item.getMedicineId())) {
                return false;
            }
            addedIds.add(item.getMedicineId());
            if (item.getDiscount() == null || item.getDiscount() <= 0 || item.getDiscount() >= 100) {
                return false;
            }
        }

        // Otherwise it is a valid promotion creation command
        return true;
    }
}
