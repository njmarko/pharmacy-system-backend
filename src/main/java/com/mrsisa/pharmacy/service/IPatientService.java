package com.mrsisa.pharmacy.service;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.entities.Patient;
import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.entities.Recipe;
import com.mrsisa.pharmacy.domain.valueobjects.Address;
import com.mrsisa.pharmacy.dto.medicine.MedicineQRCodeReservationItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPatientService extends IJPAService<Patient> {

    Patient getPatientByIdAndActive(Long id);

    // loads reservations also to prevent lazy load errors
    Patient getPatientByIdAndReservations(Long id);

    Patient getPatientByUsernameAndActive(String username);

    Patient registerPatient(String firstName, String lastName, String username, String password, String email, String phoneNumber, Address address);


    Page<Patient> getPatients(String firstName, String lastName, Pageable pageable);

    Patient updatePatientPersonalInfo(Long id, String firstName, String lastName, String phoneNumber,
                                      String country, String city, String street, String streetNumber, String zipCode);

    Page<Medicine> getPatientAllergies(Long id, String name, Pageable pageable);

    Page<Medicine> getNotAllergicTo(Long id, String name, Pageable pageable);

    Medicine addPatientAllergy(Long id, Medicine med);

    Recipe createRecipe(Long patientId, Long pharmacyId, List<MedicineQRCodeReservationItemDTO> medicine);

    void removePatientAllergy(Long id, Long medicineId);

    void patientNotShowedUp(Long id, Long appointmentId);

    void removeAllPenalties();

    Patient findByUsernameWithAuthorities(String username);

    void addPenalties();

    Page<Pharmacy> getSubscriptionsForPatient(Long id, Pageable pageable);

    void deleteUnverifiedUsers();
}
