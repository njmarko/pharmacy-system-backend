package com.mrsisa.pharmacy.validation.constraint;

import com.mrsisa.pharmacy.validation.validator.LeaveResponseValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LeaveResponseValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface LeaveResponseConstraint {
    String message() default "Invalid leave days request response.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
