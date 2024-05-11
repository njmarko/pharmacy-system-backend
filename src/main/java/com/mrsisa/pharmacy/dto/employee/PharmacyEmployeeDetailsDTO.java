package com.mrsisa.pharmacy.dto.employee;

import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.dto.WorkingDayDTO;
import lombok.Data;

import java.util.List;

@Data
public class PharmacyEmployeeDetailsDTO {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private EmployeeType employeeType;
    private Double employeeAverageGrade;
    private List<WorkingDayDTO> workingHours;

}
