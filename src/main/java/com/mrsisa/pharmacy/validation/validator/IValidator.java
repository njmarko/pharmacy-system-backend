package com.mrsisa.pharmacy.validation.validator;

public interface IValidator<T> {
    void isValid(T entity);
}
