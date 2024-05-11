package com.mrsisa.pharmacy.constants;

import com.mrsisa.pharmacy.dto.WorkingDayDTO;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class PharmacyPharmacistConstants {
    public static final Long PHARMACY_ID = 1L;
    public static final String PHARMACY_NAME = "Benu Apoteka";
    public static final String PHARMACIST_FIRST_NAME = "nekorandomime";
    public static final String PHARMACIST_LAST_NAME = "nekorandomprezime";
    public static final String PHARMACIST_USERNAME = "nekirandomusername";
    public static final String PHARMACIST_EMAIL = "nekirandommejl@gmail.com";
    public static final String PHARMACIST_PASSWORD = "test123";
    public static final List<WorkingDayDTO> PHARMACiST_WORKING_HOURS = List.of(
            new WorkingDayDTO(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 0)),
            new WorkingDayDTO(DayOfWeek.WEDNESDAY, LocalTime.of(9, 30), LocalTime.of(12, 0))
    );

}
