package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.ComplaintType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaint")
@Getter
@Setter
public class Complaint extends BaseEntity {

    @Column(name = "title", nullable = false, length = 1000)
    private String title;

    @Column(name = "entity", nullable = false)
    private String entity;

    @Column(name = "date_posted", nullable = false)
    private LocalDateTime datePosted;

    @Column(name = "complaint_type", nullable = false)
    @Enumerated
    private ComplaintType complaintType;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private Patient patient;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "reply_id")
    private ComplaintReply reply;

    public Complaint() {
        super();
    }

    public Complaint(String title, LocalDateTime datePosted, ComplaintType complaintType) {
        this();
        this.setTitle(title);
        this.setDatePosted(datePosted);
        this.setComplaintType(complaintType);
    }

    public Complaint(String title, LocalDateTime datePosted, ComplaintType complaintType, Patient patient,String entity) {
        this(title, datePosted, complaintType);
        this.setPatient(patient);
        this.setEntity(entity);
    }

    public Complaint(String title, LocalDateTime datePosted, ComplaintType complaintType, Patient patient, String entity, ComplaintReply reply) {
        this(title, datePosted, complaintType, patient, entity);
        this.setReply(reply);
    }
}
