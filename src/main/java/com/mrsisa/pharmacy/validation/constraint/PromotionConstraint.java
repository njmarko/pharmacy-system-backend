package com.mrsisa.pharmacy.validation.constraint;

import com.mrsisa.pharmacy.validation.validator.PromotionValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PromotionValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PromotionConstraint {
    String message() default "Invalid promotion data.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
