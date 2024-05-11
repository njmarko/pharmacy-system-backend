package com.mrsisa.pharmacy.aspect.impl;

import com.mrsisa.pharmacy.aspect.OwningPatientNotPenalized;
import com.mrsisa.pharmacy.service.IPatientService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class OwningPatientNotPenalizedAspect extends OwningAspectBase {
    private final IPatientService patientService;

    @Autowired
    public OwningPatientNotPenalizedAspect(IPatientService patientService) {
        this.patientService = patientService;
    }

    @Before("@annotation(com.mrsisa.pharmacy.aspect.OwningPatientNotPenalized)")
    public void patientOwnsResourceAndNotPenalized(JoinPoint joinPoint) {
        Long patientId = getIdentityParameter(joinPoint, OwningPatientNotPenalized.class);
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var patient = patientService.findByUsernameWithAuthorities(authentication.getName());
        if (!patient.getId().equals(patientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permissions to access this data.");
        }

        String message = getMethodAnnotation(joinPoint, OwningPatientNotPenalized.class).message();

        String actionName = getMethodAnnotation(joinPoint, OwningPatientNotPenalized.class).actionName();

        if (actionName.isBlank()) {
            actionName = "this action";
        }

        if (patient.getNumPenalties() >= 3) {
            if (!message.isBlank()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to perform " + actionName +
                        " because you have accumulated 3 or more penalty points this month!");
            }
        }
    }

}
