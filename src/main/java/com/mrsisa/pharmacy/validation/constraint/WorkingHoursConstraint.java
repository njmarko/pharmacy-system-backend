package com.mrsisa.pharmacy.validation.constraint;

import com.mrsisa.pharmacy.validation.validator.WorkingHoursValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorkingHoursValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkingHoursConstraint {
    String message() default "Invalid working hours.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
