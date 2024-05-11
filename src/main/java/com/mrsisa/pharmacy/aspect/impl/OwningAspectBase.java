package com.mrsisa.pharmacy.aspect.impl;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public abstract class OwningAspectBase {

    protected Long getIdentityParameter(JoinPoint joinPoint, Class<? extends Annotation> owningAnnotation) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var method = methodSignature.getMethod();
        var owner = method.getAnnotation(owningAnnotation);
        var argumentValues = joinPoint.getArgs();
        try {
            var identifier = owner.getClass().getMethod("identifier");
            return (Long) argumentValues[Arrays.asList(methodSignature.getParameterNames()).indexOf(identifier.invoke(owner))];
        } catch (Exception exception) {
            return null;
        }
    }

    protected Object getParameter(JoinPoint joinPoint, Class<? extends Annotation> owningAnnotation, String parameterName) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var method = methodSignature.getMethod();
        var owner = method.getAnnotation(owningAnnotation);
        var argumentValues = joinPoint.getArgs();
        try {
            var param = owner.getClass().getMethod(parameterName);
            return argumentValues[Arrays.asList(methodSignature.getParameterNames()).indexOf(param.invoke(owner))];
        } catch (Exception exception) {
            return null;
        }
    }

    protected <T extends Annotation> T getMethodAnnotation(JoinPoint joinPoint, Class<T> annotation) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        var method = methodSignature.getMethod();
        return method.getAnnotation(annotation);
    }

}
