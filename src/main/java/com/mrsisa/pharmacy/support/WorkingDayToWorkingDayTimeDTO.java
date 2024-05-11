package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import com.mrsisa.pharmacy.dto.WorkingDayTimeDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class WorkingDayToWorkingDayTimeDTO extends AbstractConverter<WorkingDay, WorkingDayTimeDTO>
        implements IConverter<WorkingDay, WorkingDayTimeDTO> {

    @Override
    public WorkingDayTimeDTO convert(@NonNull WorkingDay workingDay) {

        var workingDayTimeDTO = new WorkingDayTimeDTO();
        workingDayTimeDTO.setFromHours(workingDay.getFromHours());
        workingDayTimeDTO.setToHours(workingDay.getToHours());
        workingDayTimeDTO.setDayInWeek(workingDay.getDay().getValue());

        return workingDayTimeDTO;
    }
}

