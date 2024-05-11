package com.mrsisa.pharmacy.support;

import org.springframework.core.convert.converter.Converter;

import java.util.Collection;
import java.util.stream.Stream;

public interface IConverter<TFrom, TTo> extends Converter<TFrom, TTo> {
    Collection<TTo> convert(Collection<TFrom> from);
    Stream<TTo> convert(Stream<TFrom> from);
}
