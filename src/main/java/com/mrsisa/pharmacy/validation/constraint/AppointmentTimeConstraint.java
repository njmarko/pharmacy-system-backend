package com.mrsisa.pharmacy.validation.constraint;

import com.mrsisa.pharmacy.validation.validator.AppointmentTimeConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AppointmentTimeConstraintValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AppointmentTimeConstraint {
    String message() default "Invalid appointment time.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
