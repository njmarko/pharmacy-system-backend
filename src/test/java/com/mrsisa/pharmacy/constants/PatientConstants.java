package com.mrsisa.pharmacy.constants;

import com.mrsisa.pharmacy.domain.enums.EmployeeType;

public class PatientConstants {
    public static final Long DB_PATIENT_ID = 1L;

    public static final Long DB_OLD_PATIENT_ID = 1L;
    public static final String DB_OLD_PATIENT_FIRST_NAME = "Old first mame";
    public static final String DB_OLD_PATIENT_LAST_NAME = "Old last name";
    public static final String DB_OLD_PATIENT_PHONE = "Old phone number";
    public static final String DB_OLD_PATIENT_COUNTRY = "Old Country";
    public static final String DB_OLD_PATIENT_CITY = "Old city";
    public static final String DB_OLD_PATIENT_STREET = "Old street";
    public static final String DB_OLD_PATIENT_STREET_NUM = "Old street number";
    public static final String DB_OLD_PATIENT_ZIP_CODE = "Old zip code";

    // since this data is used for updating, ID will not be changes and will still be 1L
    public static final Long DB_NEW_PATIENT_ID = 1L;
    public static final String DB_NEW_PATIENT_FIRST_NAME = "New first mame";
    public static final String DB_NEW_PATIENT_LAST_NAME = "New last name";
    public static final String DB_NEW_PATIENT_PHONE = "New phone number";
    public static final String DB_NEW_PATIENT_COUNTRY = "New Country";
    public static final String DB_NEW_PATIENT_CITY = "New city";
    public static final String DB_NEW_PATIENT_STREET = "New street";
    public static final String DB_NEW_PATIENT_STREET_NUM = "New street number";
    public static final String DB_NEW_PATIENT_ZIP_CODE = "New zip code";

    public static final Integer PAST_DERM_APPOINTMENTS_NUM = 7;
    public static final String PAST_DERM_APPOINTMENTS_DERM1_NAME = "Andrea";
    public static final String PAST_DERM_APPOINTMENTS_DERM2_NAME = "Divna";
    public static final Long DB_DEJAN_DJORDJEVIC_ID = 5L;

    public static final Integer PAGE_SIZE_PAST_APPOINTMENTS = 8;

    public static final Integer PAST_DERM_APPOINTMENTS_NUM_SEARCH = 6;

    public static final Long RATE_EMPLOYEE_ID = 32L;
    public static final EmployeeType RATE_EMPLOYEE_TYPE = EmployeeType.DERMATOLOGIST;
    public static final Integer RATE_EMPLOYEE_RATING = 4;
    public static final Long RATE_EMPLOYEE_ID_WRONG = -32L;

}