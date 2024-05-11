package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Appointment;
import com.mrsisa.pharmacy.domain.valueobjects.RecipeMedicineInfo;
import com.mrsisa.pharmacy.dto.patient.ExaminedPatientDTO;
import com.mrsisa.pharmacy.dto.medicine.RecipeMedicineInfoDTO;
import com.mrsisa.pharmacy.service.IRecipeMedicineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentToExaminedPatientDTO  extends AbstractConverter<Appointment, ExaminedPatientDTO>
        implements IConverter<Appointment, ExaminedPatientDTO> {

    private final IRecipeMedicineInfoService recipeMedicineInfoService;

    @Autowired
    public AppointmentToExaminedPatientDTO(IRecipeMedicineInfoService recipeMedicineInfoService) {
        this.recipeMedicineInfoService = recipeMedicineInfoService;
    }

    @Override
    public ExaminedPatientDTO convert(@NonNull Appointment appointment) {
        var examinedPatientDTO = new ExaminedPatientDTO(appointment.getFrom(), appointment.getTo(),
                appointment.getPrice(), appointment.getPatient().getFirstName(), appointment.getPatient().getLastName(),
                appointment.getReport().getDiagnostics(), new ArrayList<>());

        if (appointment.getReport().getRecipe() != null) {
            List<RecipeMedicineInfo> recipeMedicineInfoList = recipeMedicineInfoService.getMedicinesForRecipe(appointment.getReport().getRecipe().getId());
            recipeMedicineInfoList.forEach(recipeMedicineInfo -> examinedPatientDTO.getMedicines().add(new RecipeMedicineInfoDTO(recipeMedicineInfo.getMedicine().getId(),
                    recipeMedicineInfo.getQuantity(), recipeMedicineInfo.getTherapyDays(), recipeMedicineInfo.getPrice(),
                    recipeMedicineInfo.getMedicine().getName())));
        }

        return examinedPatientDTO;
    }
}
