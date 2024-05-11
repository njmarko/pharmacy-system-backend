package com.mrsisa.pharmacy.dto;

import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    @Range(min = 0, max = 5, message = "Rating must be in the range from 0 to 5.")
    @NotNull
    private Integer rating;

    // used when rating employees
    private EmployeeType employeeType;
}
