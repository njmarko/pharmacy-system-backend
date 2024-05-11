package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface IPatientRepository extends JpaRepository<Patient, Long> {

    Patient findByUsernameAndActive(String username, Boolean active);

    @Query("select count(p) from Patient p where p.patientCategory.id = :categoryId")
    int countPatientWithCategoryId(@Param("categoryId") Long categoryId);

    //A way to get collection that has fetch lazy option
//    @Query("select p from Patient AS p join fetch p.allergicTo where p.id=:id and p.active=:active")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Patient AS p where p.id=:id and p.active=:active")
    Patient findActivePatient(@Param("id") Long id, @Param("active") Boolean active);

    @Query("select p from Patient AS p where p.id=:id and p.active=:active")
    Patient findActivePatientUnlocked(@Param("id") Long id, @Param("active") Boolean active);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Patient AS p where p.id=:id and p.active=true")
    Optional<Patient> findByIdLocked(@Param("id") Long id);

    @Query(value = "select p from Patient p where p.active=true and lower(p.firstName) like :firstName" +
            " and lower(p.lastName) like :lastName")
    Page<Patient> getPatientsSearch(@Param("firstName") String firstName,
                                    @Param("lastName") String lastName,
                                    Pageable pageable);

    @Query("select p from Patient AS p join fetch p.medicineReservations where p.id=:id")
    Patient getPatientByIdAndReservations(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Stream<Patient> findPatientsByActiveTrue();

    @Query("select p from Patient as p join fetch p.authorities " +
            "where p.username=:username " +
            "and p.active=true " +
            "and p.verified=true")
    Optional<Patient> findByUsernameFetchAuthorities(@Param("username") String username);

}
