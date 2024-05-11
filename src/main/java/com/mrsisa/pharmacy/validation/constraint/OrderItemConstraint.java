package com.mrsisa.pharmacy.validation.constraint;

import com.mrsisa.pharmacy.validation.validator.OrderItemValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OrderItemValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderItemConstraint {
    String message() default "Invalid order item.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
