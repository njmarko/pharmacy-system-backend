package com.mrsisa.pharmacy.dto;

import com.mrsisa.pharmacy.validation.constraint.WorkingHoursConstraint;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class HireDermatologistDTO {

    @NotBlank(message = "Dermatologist username can't be empty.")
    private String dermatologistUsername;

    @WorkingHoursConstraint
    private List<WorkingDayDTO> workingDays;

}
