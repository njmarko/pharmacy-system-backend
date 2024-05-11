package com.mrsisa.pharmacy.dto.complaint;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ComplaintCreationDTO {

    private Long patientId;

    @NotBlank(message = "Complaint content cannot be blank.")
    @Length(max = 1000, message = "Complaint content cannot be longer than 1000 characters.")
    private String content;
}
