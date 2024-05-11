package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.domain.valueobjects.Address;
import com.mrsisa.pharmacy.repository.IMedicineRepository;
import com.mrsisa.pharmacy.repository.IPatientRepository;
import com.mrsisa.pharmacy.service.impl.PatientService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.mrsisa.pharmacy.constants.MedicineConstants.*;
import static com.mrsisa.pharmacy.constants.PatientConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class PatientServiceTest {

    @Mock
    private IPatientRepository patientRepositoryMock;

    @Mock
    private IMedicineRepository medicineRepositoryMock;

    @Mock
    private IPatientService patientServiceMock;

    @Mock
    private Patient patientMock;

    @InjectMocks
    private PatientService patientService;


    @Test
    void testGetPatientByIdAndActive() {

        when(patientRepositoryMock.findActivePatient(DB_PATIENT_ID, Boolean.TRUE))
                .thenReturn(Optional.of(patientMock).orElse(null));

        Patient dbPatient = patientService.getPatientByIdAndActive(DB_PATIENT_ID);

        assertEquals(patientMock, dbPatient);

        verify(patientRepositoryMock, times(1)).findActivePatient(DB_PATIENT_ID, Boolean.TRUE);

        verifyNoMoreInteractions(patientRepositoryMock);

    }

    @Test
    void testGetPatientAllergies() {
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        Medicine medMock1 = new Medicine();
        medMock1.setId(DB_MEDICINE_ID_1);
        medMock1.setName(DB_MED_NAME_1);

        Medicine medMock2 = new Medicine();
        medMock2.setId(DB_MEDICINE_ID_2);
        medMock2.setName(DB_MED_NAME_2);

        // No name or sort params
        when(medicineRepositoryMock.findPatientAllergies(DB_PATIENT_ID, "", pageRequest))
                .thenReturn(new PageImpl<Medicine>(Arrays.asList(medMock1, medMock2)));


        Page<Medicine> allergies = patientService.getPatientAllergies(DB_PATIENT_ID, "", pageRequest);

        assertThat(allergies).hasSize(2);

        verify(medicineRepositoryMock, times(1))
                .findPatientAllergies(DB_PATIENT_ID, "", pageRequest);

        verifyNoMoreInteractions(medicineRepositoryMock);

    }

    @Test
    void testGetPatientAllergiesSearchName() {
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);
        Medicine medMock1 = new Medicine();
        medMock1.setId(DB_MEDICINE_ID_1);
        medMock1.setName(DB_MED_NAME_1);

        Medicine medMock2 = new Medicine();
        medMock2.setId(DB_MEDICINE_ID_2);
        medMock2.setName(DB_MED_NAME_2);

        // name aspirin uppercase
        // it has to be lovercase name here because service will convert it to lovercase
        when(medicineRepositoryMock.findPatientAllergies(DB_PATIENT_ID, "aspirin", pageRequest))
                .thenReturn(new PageImpl<Medicine>(Collections.singletonList(medMock1)));

        // name aspirin uppercase

        Page<Medicine> allergies_search = patientService
                .getPatientAllergies(DB_PATIENT_ID, "ASPIRIN", pageRequest);

        assertThat(allergies_search).hasSize(1);

        verify(medicineRepositoryMock, times(1))
                .findPatientAllergies(DB_PATIENT_ID, "aspirin", pageRequest);

        verifyNoMoreInteractions(medicineRepositoryMock);
    }

    @Test
    void testGetPatientAllergiesSortName() {
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "name"));
        Medicine medMock1 = new Medicine();
        medMock1.setId(DB_MEDICINE_ID_1);
        medMock1.setName(DB_MED_NAME_1);

        Medicine medMock2 = new Medicine();
        medMock2.setId(DB_MEDICINE_ID_2);
        medMock2.setName(DB_MED_NAME_2);

        // sorted by name descending
        when(medicineRepositoryMock.findPatientAllergies(DB_PATIENT_ID, "", pageRequest))
                .thenReturn(new PageImpl<Medicine>(Arrays.asList(medMock2, medMock1)));

        // sorted by name descending

        Page<Medicine> allergies_search = patientService.getPatientAllergies(DB_PATIENT_ID, "", pageRequest);

        assertThat(allergies_search).hasSize(2);

        assertThat(allergies_search.getContent().get(0)).isEqualTo(medMock2);
        assertThat(allergies_search.getContent().get(1)).isEqualTo(medMock1);

        verify(medicineRepositoryMock, times(1))
                .findPatientAllergies(DB_PATIENT_ID, "", pageRequest);

        verifyNoMoreInteractions(medicineRepositoryMock);
    }


    @Test
    void testGetPatientAllergiesPagination() {
        PageRequest pageRequest = PageRequest.of(1, 1);
        Medicine medMock1 = new Medicine();
        medMock1.setId(DB_MEDICINE_ID_1);
        medMock1.setName(DB_MED_NAME_1);

        Medicine medMock2 = new Medicine();
        medMock2.setId(DB_MEDICINE_ID_2);
        medMock2.setName(DB_MED_NAME_2);

        // page 1 with one item per page
        when(medicineRepositoryMock.findPatientAllergies(DB_PATIENT_ID, "", pageRequest))
                .thenReturn(new PageImpl<Medicine>(Collections.singletonList(medMock2)));

        // page 1 with one item per page

        Page<Medicine> allergies_search = patientService.getPatientAllergies(DB_PATIENT_ID, "", pageRequest);

        assertThat(allergies_search).hasSize(1);

        assertThat(allergies_search.getContent().get(0)).isEqualTo(medMock2);

        verify(medicineRepositoryMock, times(1))
                .findPatientAllergies(DB_PATIENT_ID, "", pageRequest);

        verifyNoMoreInteractions(medicineRepositoryMock);
    }


    @Test()
    void testGetPatientAllergiesWrongSortParam() {
        PageRequest pageRequest = PageRequest
                .of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, NON_EXISTENT_SORT_PARAM));
        Medicine medMock1 = new Medicine();
        medMock1.setId(DB_MEDICINE_ID_1);
        medMock1.setName(DB_MED_NAME_1);

        Medicine medMock2 = new Medicine();
        medMock2.setId(DB_MEDICINE_ID_2);
        medMock2.setName(DB_MED_NAME_2);

        // wrong sort params
        when(medicineRepositoryMock.findPatientAllergies(DB_PATIENT_ID, "", pageRequest))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bas search or sort parameters."));

        // wrong sort params

        assertThrows(ResponseStatusException.class, () -> {
            Page<Medicine> allergies_search = patientService.getPatientAllergies(DB_PATIENT_ID, "", pageRequest);

        });

        verify(medicineRepositoryMock, times(1))
                .findPatientAllergies(DB_PATIENT_ID, "", pageRequest);

        verifyNoMoreInteractions(medicineRepositoryMock);
    }

    @Test()
    @Transactional
    void testUpdatePatientPersonalInfo() {

        Patient oldPatient = new Patient();
        oldPatient.setId(DB_OLD_PATIENT_ID);
        oldPatient.setFirstName(DB_OLD_PATIENT_FIRST_NAME);
        oldPatient.setLastName(DB_OLD_PATIENT_LAST_NAME);
        oldPatient.setPhoneNumber(DB_OLD_PATIENT_PHONE);
        oldPatient.setAddress(new Address());
        oldPatient.getAddress().setCountry(DB_OLD_PATIENT_COUNTRY);
        oldPatient.getAddress().setCity(DB_OLD_PATIENT_CITY);
        oldPatient.getAddress().setStreet(DB_OLD_PATIENT_STREET);
        oldPatient.getAddress().setStreetNumber(DB_OLD_PATIENT_STREET_NUM);
        oldPatient.getAddress().setZipCode(DB_OLD_PATIENT_ZIP_CODE);

        // wrong sort params
        when(patientRepositoryMock.findById(DB_OLD_PATIENT_ID))
                .thenReturn(Optional.of(oldPatient));

        Patient newPatient = new Patient();
        newPatient.setId(DB_NEW_PATIENT_ID);
        newPatient.setFirstName(DB_NEW_PATIENT_FIRST_NAME);
        newPatient.setLastName(DB_NEW_PATIENT_LAST_NAME);
        newPatient.setPhoneNumber(DB_NEW_PATIENT_PHONE);
        newPatient.setAddress(new Address());
        newPatient.getAddress().setCountry(DB_NEW_PATIENT_COUNTRY);
        newPatient.getAddress().setCity(DB_NEW_PATIENT_CITY);
        newPatient.getAddress().setStreet(DB_NEW_PATIENT_STREET);
        newPatient.getAddress().setStreetNumber(DB_NEW_PATIENT_STREET_NUM);
        newPatient.getAddress().setZipCode(DB_NEW_PATIENT_ZIP_CODE);


        when(patientRepositoryMock.saveAndFlush(oldPatient)).thenReturn(newPatient);


        // using spy to set what is returned from this one method in the same service
        PatientService patientServiceSpy = Mockito.spy(patientService);
        Mockito.doReturn(newPatient).when(patientServiceSpy).getPatientByIdAndActive(oldPatient.getId());


        // Action
        Patient returnedPatient = patientServiceSpy.updatePatientPersonalInfo(DB_OLD_PATIENT_ID, DB_NEW_PATIENT_FIRST_NAME,
                DB_NEW_PATIENT_LAST_NAME, DB_NEW_PATIENT_PHONE, DB_NEW_PATIENT_COUNTRY, DB_NEW_PATIENT_CITY,
                DB_NEW_PATIENT_STREET, DB_NEW_PATIENT_STREET_NUM, DB_NEW_PATIENT_ZIP_CODE);


        assertEquals(newPatient, returnedPatient);


        verify(patientRepositoryMock, times(1)).findById(DB_OLD_PATIENT_ID);
        verify(patientRepositoryMock, times(1)).saveAndFlush(oldPatient);


        verifyNoMoreInteractions(medicineRepositoryMock);
        verifyNoMoreInteractions(patientServiceMock);
    }

    @Test()
    @Transactional
    void testUpdatePatientPersonalInfoWrongPatientId() {

        Patient oldPatient = new Patient();
        oldPatient.setId(DB_OLD_PATIENT_ID);
        oldPatient.setFirstName(DB_OLD_PATIENT_FIRST_NAME);
        oldPatient.setLastName(DB_OLD_PATIENT_LAST_NAME);
        oldPatient.setPhoneNumber(DB_OLD_PATIENT_PHONE);
        oldPatient.setAddress(new Address());
        oldPatient.getAddress().setCountry(DB_OLD_PATIENT_COUNTRY);
        oldPatient.getAddress().setCity(DB_OLD_PATIENT_CITY);
        oldPatient.getAddress().setStreet(DB_OLD_PATIENT_STREET);
        oldPatient.getAddress().setStreetNumber(DB_OLD_PATIENT_STREET_NUM);
        oldPatient.getAddress().setZipCode(DB_OLD_PATIENT_ZIP_CODE);

        // wrong sort params
        when(patientRepositoryMock.findById(DB_OLD_PATIENT_ID))
                .thenReturn(Optional.empty());

        Patient newPatient = new Patient();
        newPatient.setId(DB_NEW_PATIENT_ID);
        newPatient.setFirstName(DB_NEW_PATIENT_FIRST_NAME);
        newPatient.setLastName(DB_NEW_PATIENT_LAST_NAME);
        newPatient.setPhoneNumber(DB_NEW_PATIENT_PHONE);
        newPatient.setAddress(new Address());
        newPatient.getAddress().setCountry(DB_NEW_PATIENT_COUNTRY);
        newPatient.getAddress().setCity(DB_NEW_PATIENT_CITY);
        newPatient.getAddress().setStreet(DB_NEW_PATIENT_STREET);
        newPatient.getAddress().setStreetNumber(DB_NEW_PATIENT_STREET_NUM);
        newPatient.getAddress().setZipCode(DB_NEW_PATIENT_ZIP_CODE);


        when(patientRepositoryMock.saveAndFlush(oldPatient)).thenReturn(newPatient);


        // using spy to set what is returned from this one method in the same service
        PatientService patientServiceSpy = Mockito.spy(patientService);
        Mockito.doReturn(newPatient).when(patientServiceSpy).getPatientByIdAndActive(oldPatient.getId());


        // Action
        assertThrows(ResponseStatusException.class, () -> {
            Patient returnedPatient = patientServiceSpy.updatePatientPersonalInfo(DB_OLD_PATIENT_ID, DB_NEW_PATIENT_FIRST_NAME,
                    DB_NEW_PATIENT_LAST_NAME, DB_NEW_PATIENT_PHONE, DB_NEW_PATIENT_COUNTRY, DB_NEW_PATIENT_CITY,
                    DB_NEW_PATIENT_STREET, DB_NEW_PATIENT_STREET_NUM, DB_NEW_PATIENT_ZIP_CODE);

        });

        verifyNoMoreInteractions(medicineRepositoryMock);
        verifyNoMoreInteractions(patientServiceMock);
    }


}
