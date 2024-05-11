package com.mrsisa.pharmacy.domain.entities;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_reply")
@Getter
@Setter
public class ComplaintReply extends BaseEntity {

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "date_replied", nullable = false)
    private LocalDateTime dateReplied;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    private SystemAdmin systemAdmin;

    @OneToOne(mappedBy = "reply", cascade = {}, fetch = FetchType.EAGER)
    private Complaint complaint;

    public ComplaintReply() {
        super();
    }

    public ComplaintReply(String content, LocalDateTime dateReplied, SystemAdmin systemAdmin, Complaint complaint) {
        this.setContent(content);
        this.setDateReplied(dateReplied);
        this.setSystemAdmin(systemAdmin);
        this.setComplaint(complaint);
    }

}
