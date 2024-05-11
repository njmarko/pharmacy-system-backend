package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.valueobjects.WorkingDay;
import com.mrsisa.pharmacy.dto.WorkingDayDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class WorkingDayDTOToWorkingDay extends AbstractConverter<WorkingDayDTO, WorkingDay> implements IConverter<WorkingDayDTO, WorkingDay> {
    @Override
    public WorkingDay convert(@NonNull WorkingDayDTO workingDayDTO) {
        return getModelMapper().map(workingDayDTO, WorkingDay.class);
    }
}
