package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.dto.leavedays.LeaveDaysRequestDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class LeaveDaysRequestToLeaveDaysRequestDTO extends AbstractConverter<LeaveDaysRequest, LeaveDaysRequestDTO> {
    @Override
    public LeaveDaysRequestDTO convert(@NonNull LeaveDaysRequest leaveDaysRequest) {
        return getModelMapper().map(leaveDaysRequest, LeaveDaysRequestDTO.class);
    }
}
