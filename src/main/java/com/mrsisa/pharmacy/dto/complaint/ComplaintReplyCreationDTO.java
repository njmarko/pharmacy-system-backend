package com.mrsisa.pharmacy.dto.complaint;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintReplyCreationDTO {
    private Long adminId;

    @NotBlank(message = "Complaint reply cannot be blank.")
    @Length(max = 1000, message = "Complaint reply cannot be longer than 1000 characters.")
    private String content;

}
