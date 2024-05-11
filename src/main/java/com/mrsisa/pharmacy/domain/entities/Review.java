package com.mrsisa.pharmacy.domain.entities;

import com.mrsisa.pharmacy.domain.enums.ReviewType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "review")
@Getter
@Setter
public class Review extends BaseEntity {

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Column(name = "date_posted", nullable = false)
    private LocalDate datePosted;

    @Column(name = "review_type", nullable = false)
    @Enumerated
    private ReviewType reviewType;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    private Patient reviewer;

    public Review() {
        super();
    }

    public Review(Integer grade, LocalDate datePosted, ReviewType reviewType, Patient reviewer) {
        this();
        this.setGrade(grade);
        this.setDatePosted(datePosted);
        this.setReviewType(reviewType);
        this.setReviewer(reviewer);
    }
}
