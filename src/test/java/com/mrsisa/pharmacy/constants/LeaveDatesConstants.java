package com.mrsisa.pharmacy.constants;

import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.LeaveDaysRequestStatus;

public class LeaveDatesConstants {
    public static final String LEAVE_FROM_DATE = "2021-06-01T00:00:00";
    public static final String LEAVE_TO_DATE="2021-06-30T00:00:00";
    public static final Integer NUM_OF_APPOINTMENTS = 3;
    public static final Integer NUM_OF_LEAVE_REQUESTS = 1;
    public static final AppointmentStatus APPOINTMENT_STATUS_CANCELED = AppointmentStatus.CANCELED;
    public static final LeaveDaysRequestStatus LEAVE_REQUEST_STATUS_REJECTED = LeaveDaysRequestStatus.REJECTED;
}
