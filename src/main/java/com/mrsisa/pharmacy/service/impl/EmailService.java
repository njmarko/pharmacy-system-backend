package com.mrsisa.pharmacy.service.impl;


import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.LeaveDaysRequestStatus;
import com.mrsisa.pharmacy.service.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.time.format.DateTimeFormatter;

@Component
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;

    private static final String DEAR = "Dear ";
    private static final String DEAR_END = ",\n\n";
    private static final String ALL_THE_BEST = "\nAll the best,\n";
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm";

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        var message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


    @Override
    public void sendIssuedReservationMessage(MedicineReservation medicineReservation) {
        var to = medicineReservation.getPatient().getEmail();
        var subject = "Issued reservation";
        var sb = new StringBuilder();
        sb.append(DEAR).append(medicineReservation.getPatient().getFirstName()).append(DEAR_END);
        sb.append("The following medicines have been issued to you with total price " + "of ").
                append(medicineReservation.getPrice()).append("RSD :").append("\n");
        medicineReservation.getReservedMedicines().forEach(item ->
                sb.append("\t\t").append(item.getMedicine().getName()).append(" ").append(item.getQuantity())
                        .append("pcs - ").append(item.getPrice()).append("RSD\n"));

        sb.append(ALL_THE_BEST).append(medicineReservation.getPharmacy().getName()).append(".");
        sendSimpleMessage(to, subject, sb.toString());
    }

    @Override
    public void sendDrugReservationCreatedMessage(MedicineReservation medicineReservation) {
        var to = medicineReservation.getPatient().getEmail();
        var subject = "Reservation " + medicineReservation.getId() + " at " + medicineReservation.getPharmacy().getName();
        var sb = new StringBuilder();
        sb.append(DEAR).append(medicineReservation.getPatient().getFirstName()).append(DEAR_END);
        sb.append("Id of reservation is ").append(medicineReservation.getId()).append("\n\n");
        sb.append("The following medicines have been reserved for you with total price of ").
                append(medicineReservation.getPrice()).append("RSD :").append("\n");
        medicineReservation.getReservedMedicines().forEach(item ->
                sb.append("\t\t").append(item.getMedicine().getName()).append(" ").append(item.getQuantity())
                        .append(" pcs - ").append(item.getPrice()).append("RSD\n"));

        sb.append(ALL_THE_BEST).append(medicineReservation.getPharmacy().getName()).append(".");
        sendSimpleMessage(to, subject, sb.toString());
    }

    @Override
    @Async
    public void sendConfirmationMessage(String username, String to, String activationLink) throws MessagingException {
        var msgContent = new StringBuilder();

        var message = mailSender.createMimeMessage();

        var helper = new MimeMessageHelper(message, false);

        helper.setTo(to);

        helper.setSubject("Account activation");

        msgContent.append("<h1>Hello ");
        msgContent.append(username);
        msgContent.append("!");
        msgContent.append("</h1>");

        msgContent.append("<p>In order to use the application, first you need to verify your account.");
        msgContent.append("<br>In order to to that, click ");
        msgContent.append("<a href='");
        msgContent.append(activationLink);
        msgContent.append("'>here.</a>");

        msgContent.append("<br><br>Admin team</p>");

        helper.setText(msgContent.toString(), true);

        mailSender.send(message);

    }

    @Override
    public void sendDermatologistAppointmentScheduledMessage(Appointment scheduled) {
        var to = scheduled.getPatient().getEmail();
        var subject = "Scheduled Dermatologist appointment " + scheduled.getId() + " at " + scheduled.getEmployee().getPharmacy().getName();
        var sb = new StringBuilder();
        sb.append(DEAR).append(scheduled.getPatient().getFirstName()).append(DEAR_END);
        sb.append("Id of scheduled appointment with Dermatologist is ").append(scheduled.getId()).append("\n\n");
        sb.append("Pharmacy: ").append(scheduled.getEmployee().getPharmacy().getName()).append(DEAR_END);
        sb.append("Dermatologist: ").append(scheduled.getEmployee().getPharmacyEmployee().getFirstName()).append(" ")
                .append(scheduled.getEmployee().getPharmacyEmployee().getLastName()).append(DEAR_END);
        sb.append("Start date and time: ").append(scheduled.getFrom().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).append(DEAR_END);
        sb.append("End date and time: ").append(scheduled.getTo().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).append(DEAR_END);

        sb.append(ALL_THE_BEST).append(scheduled.getEmployee().getPharmacy().getName()).append(".");
        sendSimpleMessage(to, subject, sb.toString());
    }

    @Override
    public void sendPharmacistAppointmentScheduledMessage(Appointment scheduled) {
        var to = scheduled.getPatient().getEmail();
        var subject = "Scheduled Pharmacist appointment " + scheduled.getId() + " at " + scheduled.getEmployee().getPharmacy().getName();
        var sb = new StringBuilder();
        sb.append(DEAR).append(scheduled.getPatient().getFirstName()).append(DEAR_END);
        sb.append("Id of scheduled appointment with Pharmacist is ").append(scheduled.getId()).append("\n\n");
        sb.append("Pharmacy: ").append(scheduled.getEmployee().getPharmacy().getName()).append(DEAR_END);
        sb.append("Pharmacist: ").append(scheduled.getEmployee().getPharmacyEmployee().getFirstName()).append(" ")
                .append(scheduled.getEmployee().getPharmacyEmployee().getLastName()).append(DEAR_END);
        sb.append("Start date and time: ").append(scheduled.getFrom().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).append(DEAR_END);
        sb.append("End date and time: ").append(scheduled.getTo().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).append(DEAR_END);

        sb.append(ALL_THE_BEST).append(scheduled.getEmployee().getPharmacy().getName()).append(".");
        sendSimpleMessage(to, subject, sb.toString());
    }

    @Override
    @Async
    public void notifySupplier(Offer offer) {
        var to = offer.getSupplier().getEmail();
        var subject = "Order " + offer.getOriginalOrder().getId() + " results";
        var sb = new StringBuilder();
        sb.append(DEAR).append(offer.getSupplier().getFirstName()).append(DEAR_END);
        if(offer.getOfferStatus().toString().equals(""))
            return;
        var result = offer.getOfferStatus().toString().toLowerCase();
        sb.append("Your offer ").append(offer.getId()).append(" has been ").append(result).append(".\n");
        sb.append(ALL_THE_BEST).append(offer.getOriginalOrder().getPharmacy().getName()).append(".");
        sendSimpleMessage(to, subject, sb.toString());
    }

    @Override
    @Async
    public void notifyEmployeeAboutLeaveRequestResponse(LeaveDaysRequest request) {
        var to = request.getEmployee().getEmail();
        var subject = "Leave days request response";
        var sb = new StringBuilder();
        sb.append(DEAR).append(request.getEmployee().getFirstName()).append(DEAR_END);
        sb.append("We inform you that your request for leave days in a period ");
        var status = request.getLeaveDaysRequestStatus() == LeaveDaysRequestStatus.APPROVED ? "approved" : "rejected";
        sb.append(request.getFrom().toString()).append(" - ").append(request.getTo().toString()).append(" has been ").append(status).append(".\n");
        if (request.getLeaveDaysRequestStatus() == LeaveDaysRequestStatus.REJECTED) {
            sb.append("Rejection reason: ").append(request.getRejection().getReason()).append(".\n");
        }
        sb.append(ALL_THE_BEST).append("MRS-ISA T1").append(".");
        sendSimpleMessage(to, subject, sb.toString());
    }

    @Override
    public void sendRecipeConfirmationMail(Patient patient, Recipe recipe) {
        var to = patient.getEmail();
        var subject = "Recipe creation";
        var sb = new StringBuilder();
        sb.append(DEAR).append(patient.getUsername()).append(DEAR_END);
        sb.append("This is a confirmation email for recipe with id ").append(recipe.getId());
        sb.append(" at pharmacy ").append(recipe.getPharmacy().getName()).append(".");
        sb.append("\n\n");
        sb.append("Admin team");
        sendSimpleMessage(to, subject, sb.toString());
    }

    @Override
    public void sendComplaintReplyNotification(Patient patient, Complaint complaint) {
        var to = patient.getEmail();
        var subject = "Complaint reply";
        var sb = new StringBuilder();
        sb.append(DEAR).append(patient.getUsername()).append(DEAR_END);
        sb.append("Your complaint against ").append(complaint.getEntity()).append(" has been responded to by ").append(complaint.getReply().getSystemAdmin().getUsername()).append(":\n\n");
        sb.append("''").append(complaint.getReply().getContent()).append("''");
        sendSimpleMessage(to, subject, sb.toString());
    }

    @Override
    public void notifySubscriberAboutPromotion(Patient subscriber, Promotion promotion) {
        var to = subscriber.getEmail();
        var subject = "New promotion in " + promotion.getPharmacy().getName();
        var sb = new StringBuilder();
        sb.append(DEAR).append(subscriber.getUsername()).append(DEAR_END);
        sb.append("A new promotion has been created in ").append(promotion.getPharmacy().getName()).append(".\n\n");
        sb.append(promotion.getContent()).append("\n\n");
        sb.append("Articles which are on promotion: \n");
        promotion.getPromotionItems().forEach(item -> sb.append("\t").append(item.getMedicine().getName()).append(": -").append(item.getDiscount()).append("%\n"));
        sb.append("All the best, \n");
        sb.append(promotion.getPharmacy().getName());
        sb.append("\n\nYou are receiving these notification because you are subscribed to ").append(promotion.getPharmacy().getName()).append("'s newsletter.");
        sendSimpleMessage(to, subject, sb.toString());
    }
}
