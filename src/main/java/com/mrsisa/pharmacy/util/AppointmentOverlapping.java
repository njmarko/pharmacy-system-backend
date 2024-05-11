package com.mrsisa.pharmacy.util;

import com.mrsisa.pharmacy.domain.entities.Appointment;

import java.time.LocalDateTime;

public class AppointmentOverlapping {
    public static boolean areAppointmentsOverlapping(Appointment first, Appointment second) {
        LocalDateTime firstFrom = first.getFrom();
        LocalDateTime firstTo = first.getTo();

        LocalDateTime secondFrom = second.getFrom();
        LocalDateTime secondTo = second.getTo();

        if ((firstFrom.isAfter(secondFrom) && firstFrom.isBefore(secondTo)) ||
                (firstTo.isAfter(secondFrom) && firstTo.isBefore(secondFrom))) {
            return true;
        }

        if (firstFrom.isBefore(secondFrom) && firstTo.isAfter(secondTo)) {
            return true;
        }

        return firstFrom.isEqual(secondFrom) || firstFrom.isEqual(secondTo) ||
                firstTo.isEqual(secondFrom) || firstTo.isEqual(secondTo);
    }
}
