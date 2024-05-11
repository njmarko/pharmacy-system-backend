package com.mrsisa.pharmacy.validation.validator;

import com.mrsisa.pharmacy.dto.AddOrderItemDTO;
import com.mrsisa.pharmacy.validation.constraint.OrderItemConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OrderItemValidator implements ConstraintValidator<OrderItemConstraint, AddOrderItemDTO> {
    @Override
    public void initialize(OrderItemConstraint constraint) {
        // Empty
    }

    public boolean isValid(AddOrderItemDTO item, ConstraintValidatorContext context) {
        if (item.getIsNew() == null) {
            return false;
        }
        if (Boolean.TRUE.equals(item.getIsNew())) {
            return item.getNewPrice() != null && (item.getNewPrice() > 0.0);
        }
        return true;
    }
}
