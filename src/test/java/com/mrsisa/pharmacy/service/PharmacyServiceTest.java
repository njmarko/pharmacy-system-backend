package com.mrsisa.pharmacy.service;


import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.ReservationStatus;
import com.mrsisa.pharmacy.repository.IAppointmentRepository;
import com.mrsisa.pharmacy.repository.IMedicineReservationRepository;
import com.mrsisa.pharmacy.repository.IPharmacyRepository;
import com.mrsisa.pharmacy.repository.IRecipeRepository;
import com.mrsisa.pharmacy.service.impl.PharmacyService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PharmacyServiceTest {

    @Mock
    private IPharmacyRepository pharmacyRepositoryMock;

    @Mock
    private IAppointmentRepository appointmentRepositoryMock;

    @Mock
    private IMedicineReservationRepository medicineReservationRepositoryMock;

    @Mock
    private IRecipeRepository recipeRepositoryMock;

    @InjectMocks
    private PharmacyService pharmacyService;



    @Test
    void testSubscribe(){
        Pharmacy ph = new Pharmacy();
        Patient p = new Patient();
        p.setId(-1L);
        ph.getPromotionSubscribers().add(p);

        when(pharmacyRepositoryMock.save(ph)).thenReturn(ph);

        assertThrows(ResponseStatusException.class, ()->{
            pharmacyService.subscribe(ph, p);
        });

        verifyNoMoreInteractions(pharmacyRepositoryMock);
    }

    @Test
    void testFileComplaint(){
        Pharmacy ph = new Pharmacy();
        ph.setId(-1L);
        ph.setName("Test apoteka");
        Patient p = new Patient();
        p.setId(-1L);
        when(pharmacyRepositoryMock.getPharmacyWithComplaints(ph.getId()))
                .thenReturn(Optional.of(ph));
        when(appointmentRepositoryMock.checkIfPatientHadAppointmentWithEmployeeFromPharmacy(p.getId(), ph.getId(), AppointmentStatus.TOOK_PLACE))
                .thenReturn(0L);
        when(medicineReservationRepositoryMock.checkIfPatientHasMedicineReservationsInPharmacy(p.getId(), ph.getId(), ReservationStatus.PICKED))
                .thenReturn(0L);
        when(recipeRepositoryMock.checkIfPatientHasAnyERecipesInPharmacy(p.getId(), ph.getId()))
                .thenReturn(0L);
        when(pharmacyRepositoryMock.save(ph))
                .thenReturn(ph);

        assertThrows(ResponseStatusException.class, ()->{
           pharmacyService.fileComplaint(ph.getId(), p, "lose iskustvo");
        });
        verify(appointmentRepositoryMock, times(1)).checkIfPatientHadAppointmentWithEmployeeFromPharmacy(p.getId(), ph.getId(), AppointmentStatus.TOOK_PLACE);
        verify(medicineReservationRepositoryMock, times(1)).checkIfPatientHasMedicineReservationsInPharmacy(p.getId(), ph.getId(), ReservationStatus.PICKED);
        verify(recipeRepositoryMock, times(1)).checkIfPatientHasAnyERecipesInPharmacy(p.getId(), ph.getId());
        verify(pharmacyRepositoryMock, times(1)).getPharmacyWithComplaints(ph.getId());


    }



}
