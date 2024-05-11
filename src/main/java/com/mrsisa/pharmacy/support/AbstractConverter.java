package com.mrsisa.pharmacy.support;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractConverter<TFrom, TTo> implements IConverter<TFrom, TTo> {

    private ModelMapper modelMapper;

    public Stream<TTo> convert(Stream<TFrom> source) {
        return source.map(this::convert);
    }

    public Collection<TTo> convert(Collection<TFrom> source) {
        return this.convert(source.stream()).collect(Collectors.toList());
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }

    @Autowired
    public final void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

}
