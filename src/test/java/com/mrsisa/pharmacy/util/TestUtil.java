package com.mrsisa.pharmacy.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateSerializer;
import com.mrsisa.pharmacy.json.serializer.ISOLocalDateTimeSerializer;
import com.mrsisa.pharmacy.json.serializer.ISOLocalTimeSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestUtil {

    public static String json(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalTime.class, new ISOLocalTimeSerializer());
        module.addSerializer(LocalDate.class, new ISOLocalDateSerializer());
        module.addSerializer(LocalDateTime.class, new ISOLocalDateTimeSerializer());
        mapper.registerModule(module);
        return mapper.writeValueAsString(object);
    }
}
