package com.mrsisa.pharmacy.support;

import com.mrsisa.pharmacy.domain.entities.Complaint;
import com.mrsisa.pharmacy.dto.complaint.ComplaintDTO;
import com.mrsisa.pharmacy.dto.complaint.ComplaintReplyDTO;
import org.springframework.stereotype.Component;

@Component
public class ComplaintToComplaintDTO extends AbstractConverter<Complaint, ComplaintDTO>{
    @Override
    public ComplaintDTO convert(Complaint complaint) {

        var reply = complaint.getReply();
        var dto = new ComplaintReplyDTO();
        if(reply == null)
            dto = null;
        else{
            dto.setContent(reply.getContent());
            dto.setDatePosted(reply.getDateReplied());
            dto.setAdminUsername(reply.getSystemAdmin().getUsername());
        }


        return new ComplaintDTO(complaint.getId(), complaint.getTitle(), complaint.getPatient().getId(), dto, complaint.getComplaintType().toString(), complaint.getEntity(), complaint.getDatePosted());
    }
}
