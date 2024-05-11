package com.mrsisa.pharmacy.service;


import com.mrsisa.pharmacy.domain.entities.Complaint;
import com.mrsisa.pharmacy.domain.entities.ComplaintReply;
import com.mrsisa.pharmacy.domain.entities.SystemAdmin;
import com.mrsisa.pharmacy.repository.IComplaintRepository;
import com.mrsisa.pharmacy.repository.ISystemAdminRepository;
import com.mrsisa.pharmacy.service.impl.ComplaintService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@SpringBootTest
class ComplaintServiceTest {

    @Mock
    private IComplaintRepository complaintRepositoryMock;

    @Mock
    private ISystemAdminRepository adminRepositoryMock;

    @InjectMocks
    private ComplaintService complaintService;


    @Test
    void testWriteReply(){
        SystemAdmin admin = new SystemAdmin();
        admin.setId(-1L);
        Complaint complaint = new Complaint();
        complaint.setId(-1L);
        complaint.setReply(new ComplaintReply());

        when(complaintRepositoryMock.findByIdAndActiveTrueForUpdate(complaint.getId()))
                .thenReturn(Optional.of(complaint));
        when(adminRepositoryMock.findByIdAndActiveTrue(admin.getId()))
                .thenReturn(Optional.of(admin));
        assertThrows(ResponseStatusException.class, ()->{
           complaintService.writeReply(complaint.getId(), admin.getId(), "odgovor na zalbu");
        });

        verify(complaintRepositoryMock, times(1)).findByIdAndActiveTrueForUpdate(complaint.getId());
        verify(adminRepositoryMock, times(1)).findByIdAndActiveTrue(admin.getId());
    }
}
