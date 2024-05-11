package com.mrsisa.pharmacy.dto.employee;

import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import lombok.Data;

@Data
public class EmployeeListItemDTO {

    private Long employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeUsername;
    private EmployeeType employeeEmployeeType;

}
