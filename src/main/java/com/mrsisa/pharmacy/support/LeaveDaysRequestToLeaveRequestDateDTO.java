package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.dto.leavedays.LeaveRequestDateDTO;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LeaveDaysRequestToLeaveRequestDateDTO extends AbstractConverter<LeaveDaysRequest, LeaveRequestDateDTO>
        implements IConverter<LeaveDaysRequest, LeaveRequestDateDTO> {

    @Override
    public LeaveRequestDateDTO convert(@NonNull LeaveDaysRequest leaveDaysRequest) {
        LocalDateTime from = leaveDaysRequest.getFrom().atStartOfDay();
        LocalDateTime to = leaveDaysRequest.getTo().atTime(23, 59);


        return new LeaveRequestDateDTO(from, to, leaveDaysRequest.getLeaveDaysRequestStatus());
    }
}