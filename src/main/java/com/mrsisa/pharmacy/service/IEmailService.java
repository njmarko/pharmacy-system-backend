package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.*;
import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;

public interface IEmailService {
    @Async
    void sendSimpleMessage(String to, String subject, String text);

    @Async
    void sendIssuedReservationMessage(MedicineReservation medicineReservation);

    @Async
    void sendDrugReservationCreatedMessage(MedicineReservation medicineReservation);

    @Async
    void sendConfirmationMessage(String username, String to, String activationLink) throws MessagingException;

    @Async
    void sendDermatologistAppointmentScheduledMessage(Appointment scheduled);

    @Async
    void sendPharmacistAppointmentScheduledMessage(Appointment scheduled);

    @Async
    void notifySupplier(Offer offer);

    @Async
    void notifyEmployeeAboutLeaveRequestResponse(LeaveDaysRequest request);

    @Async
    void sendRecipeConfirmationMail(Patient patient, Recipe recipe);

    @Async
    void sendComplaintReplyNotification(Patient patient, Complaint complaint);

    @Async
    void notifySubscriberAboutPromotion(Patient subscriber, Promotion promotion);
}
