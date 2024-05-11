package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.*;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import com.mrsisa.pharmacy.repository.IAppointmentRepository;
import com.mrsisa.pharmacy.repository.IPatientRepository;
import com.mrsisa.pharmacy.repository.IPharmacyRepository;
import com.mrsisa.pharmacy.service.impl.AppointmentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class AppointmentServiceTest {

    @Mock
    private IPharmacyRepository pharmacyRepositoryMock;

    @Mock
    private IPatientRepository patientRepositoryMock;

    @Mock
    private IAppointmentRepository appointmentRepositoryMock;

    @Mock
    private IEmailService emailServiceMock;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    @Transactional
    void testScheduleAvailableDermatologistAppointment() {
        // Test case constants
        final Long APPOINTMENT_ID = 123L;
        final Long PATIENT_ID = 123L;

        // Create dummy data
        Appointment appointment = new Appointment();
        appointment.setId(APPOINTMENT_ID);
        Patient patient = new Patient();
        patient.setId(PATIENT_ID);

        // Mock repositories
        when(patientRepositoryMock.findById(PATIENT_ID)).thenReturn(Optional.of(patient));

        // Create a spy object
        AppointmentService appointmentServiceSpy = spy(appointmentService);
        doReturn(appointment).when(appointmentServiceSpy).get(APPOINTMENT_ID);

        // Verification
        appointmentServiceSpy.scheduleAvailableDermatologistAppointment(APPOINTMENT_ID, PATIENT_ID);
        assertEquals(AppointmentStatus.BOOKED,appointment.getAppointmentStatus());
        assertEquals(appointment.getPatient(), patient);
        verify(patientRepositoryMock, times(1)).findById(PATIENT_ID);
    }

    @Test
    @Transactional
    void testScheduleAppointmentForEmployee() {
        // Test case constants
        final Long PHARMACY_ID = 123L;
        final Long PATIENT_ID = 123L;
        final Double CURR_DERMATOLOGIST_PRICE = 350.0;
        final EmployeeType EMPLOYEE_TYPE = EmployeeType.DERMATOLOGIST;
        final LocalDateTime FROM_DATE = LocalDateTime.of(2021, 5, 15, 15, 0);
        final LocalDateTime TO_DATE = LocalDateTime.of(2021, 5, 15, 16, 0);

        // Create dummy data
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(PHARMACY_ID);
        pharmacy.setCurrentDermatologistAppointmentPrice(CURR_DERMATOLOGIST_PRICE);
        Patient patient = new Patient();
        patient.setId(PATIENT_ID);
        PharmacyEmployee pharmacyEmployee = new PharmacyEmployee();
        pharmacyEmployee.setEmployeeType(EMPLOYEE_TYPE);
        EmploymentContract employmentContract = new EmploymentContract();
        employmentContract.setPharmacyEmployee(pharmacyEmployee);

        // Mock repositories
        when(pharmacyRepositoryMock.findById(PHARMACY_ID)).thenReturn(Optional.of(pharmacy));
        when(patientRepositoryMock.findById(PATIENT_ID)).thenReturn(Optional.of(patient));

        // Mock email service to do nothing when it should send email to patient
        doNothing().when(emailServiceMock).sendDermatologistAppointmentScheduledMessage(any(Appointment.class));

        // Verification
        Appointment createdAppointment = appointmentService.scheduleAppointmentForEmployee(FROM_DATE, TO_DATE,
                PATIENT_ID, employmentContract, PHARMACY_ID);
        assertEquals(AppointmentStatus.BOOKED,createdAppointment.getAppointmentStatus());
        assertEquals(createdAppointment.getPrice(), CURR_DERMATOLOGIST_PRICE);
        assertEquals(createdAppointment.getEmployee().getPharmacyEmployee(), pharmacyEmployee);
        assertEquals(createdAppointment.getPatient(), patient);
        assertTrue(createdAppointment.getFrom().isEqual(FROM_DATE));
        assertTrue(createdAppointment.getTo().isEqual(TO_DATE));
        verify(pharmacyRepositoryMock, times(1)).findById(PHARMACY_ID);
        verify(patientRepositoryMock, times(1)).findById(PATIENT_ID);
        verify(appointmentRepositoryMock, times(1)).save(createdAppointment);
    }
}
