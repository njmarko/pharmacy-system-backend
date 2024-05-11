package com.mrsisa.pharmacy.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OwningPatientNotPenalized {
    String identifier() default "id";

    /**
     * Specify the name of the action that is being performed.
     * This name is used when returning the error message in case the user is not allowed to perform the action
     * because he has accumulated 3 or more penalty points for the month. <br><br>
     * <p>
     * In that case message format will be: <br><br>
     * "You are not allowed to perform &lt actionName &gt
     * because you have accumulated 3 or more penalty points this month!"
     *
     * @return String name of the actions that is being performed.
     */
    String actionName() default "this action";

    /**
     * Fully specify what message will be shown in case the user has accumulated 3 or more penalty points for the monty.
     *
     * @return String message that will be displayed
     */
    String message() default "";
}
