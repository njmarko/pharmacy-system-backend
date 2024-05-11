package com.mrsisa.pharmacy.dto.employee;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class EmployeeSearchDTO {

    private Long pharmacyId = null;
    private String firstName = "";
    private String lastName = "";
    @Range(min = 0, max = 5, message = "Low grade must be between 0 and 5.")
    private Double gradeLow = 0.0;
    @Range(min = 0, max = 5, message = "High grade must be between 0 and 5.")
    private Double gradeHigh = 5.0;

}
