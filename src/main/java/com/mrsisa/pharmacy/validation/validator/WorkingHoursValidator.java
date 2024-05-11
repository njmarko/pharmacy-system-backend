package com.mrsisa.pharmacy.validation.validator;

import com.mrsisa.pharmacy.dto.WorkingDayDTO;
import com.mrsisa.pharmacy.validation.constraint.WorkingHoursConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkingHoursValidator implements ConstraintValidator<WorkingHoursConstraint, List<WorkingDayDTO>> {
    @Override
    public void initialize(WorkingHoursConstraint constraint) {
        // Empty
    }

    public boolean isValid(List<WorkingDayDTO> workingHours, ConstraintValidatorContext context) {
        if ((workingHours == null) || (workingHours.isEmpty()) || (workingHours.size() > 7)) {
            return false;
        }
        Set<DayOfWeek> alreadyWorksThatDay = new HashSet<>();
        for (WorkingDayDTO workingDay : workingHours) {
            if (!validWorkingDay(workingDay)) {
                return false;
            }
            if (alreadyWorksThatDay.contains(workingDay.getDay())) {
                return false;
            } else {
                alreadyWorksThatDay.add(workingDay.getDay());
            }
        }
        return true;
    }

    private boolean validWorkingDay(WorkingDayDTO workingDay) {
        return allFieldsValid(workingDay) && validWorkTime(workingDay);
    }

    private boolean validWorkTime(WorkingDayDTO workingDay) {
        return workingDay.getToHours().isAfter(workingDay.getFromHours());
    }

    private boolean allFieldsValid(WorkingDayDTO workingDay) {
        return (workingDay.getDay() != null) && (workingDay.getFromHours() != null) && (workingDay.getToHours() != null);
    }
}
