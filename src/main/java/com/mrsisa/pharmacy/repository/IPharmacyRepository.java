package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Pharmacy;
import com.mrsisa.pharmacy.domain.enums.AppointmentStatus;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPharmacyRepository extends JpaRepository<Pharmacy, Long> {

    Page<Pharmacy> findAllByActive(Boolean active, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pharmacy as p where p.active = true and p.id = :id")
    Optional<Pharmacy> findByIdAndActiveTrue(@Param("id") Long id);

    @Query("select p from Pharmacy as p where p.active = true and p.id = :id")
    Optional<Pharmacy> findByIdAndActiveTrueUnlocked(@Param("id") Long id);

    List<Pharmacy> findAllByActiveTrue();

    @Query(value = "select distinct(ph) from Pharmacy ph left join fetch ph.promotionSubscribers where ph.active=true and ph.id=:id")
    Optional<Pharmacy> getPharmacyWithSubscribers(@Param("id") Long id);

    /*
    Haversine SQL formula that was used here
    http://www.plumislandmedia.net/mysql/haversine-mysql-nearest-loc/
     */
    String HAVERSINE_FORMULA = "111.045* DEGREES(ACOS(LEAST(1.0, COS(RADIANS(:latitude))\n" +
            "                 * COS(RADIANS(ph.location.latitude))\n" +
            "                 * COS(RADIANS(:longitude) - RADIANS(ph.location.longitude))\n" +
            "                 + SIN(RADIANS(:latitude))\n" +
            "                 * SIN(RADIANS(ph.location.latitude)))))";

    @Query(value = "select distinct ph from Pharmacy ph " +
            " where ph.active=true " +
            " and (lower(ph.name) like :name or lower(ph.location.address.city) like :locationAddressCity) " +
            " and ph.averageGrade >= :gradeLow and ph.averageGrade <= :gradeHigh " +
            " and " + HAVERSINE_FORMULA + "<= :distance ",
            countQuery = "select count(ph) from Pharmacy ph where ph.active=true " +
                    " and (lower(ph.name) like :name or lower(ph.location.address.city) like :locationAddressCity) " +
                    " and ph.averageGrade >= :gradeLow and ph.averageGrade <= :gradeHigh " +
                    " and " + HAVERSINE_FORMULA + "<= :distance")
    Page<Pharmacy> getPharmaciesSearchFilter(@Param("name") String name,
                                             @Param("locationAddressCity") String locationAddressCity,
                                             @Param("gradeLow") Double gradeLow,
                                             @Param("gradeHigh") Double gradeHigh,
                                             @Param("latitude") Double latitude,
                                             @Param("longitude") Double longitude,
                                             @Param("distance") Double distance,
                                             Pageable pageable);

    @Query("select distinct p from Pharmacy p left join fetch p.employees where p.id=:id and p.active=true")
    Optional<Pharmacy> findByIdWithEmployees(@Param("id") Long id);

    @Query("select distinct p from Pharmacy p left join fetch p.medicineStocks where p.id=:id and p.active=true")
    Optional<Pharmacy> findByIdWithStocks(@Param("id") Long pharmacyId);


    @Query(value = "select distinct ph from Appointment as a left join a.employee.pharmacy as ph" +
            " where ph.active = true  and a.active = true " +
            " and (lower(ph.name) like lower(:name) or lower(ph.location.address.city) like lower(:locationAddressCity)) " +
            " and ph.averageGrade >= :gradeLow and ph.averageGrade <= :gradeHigh " +
            " and :dateTime between a.from and a.to " +
            " and a.employee.pharmacyEmployee.employeeType = :employeeType " +
            " and a.appointmentStatus = :appointmentStatus " +
            " and " + HAVERSINE_FORMULA + "<= :distance "
    )
    Page<Pharmacy> getPharmaciesWithAvailablePharmacistAppointmentsOnSpecifiedDateAndtime(@Param("name") String name,
                                                                                          @Param("locationAddressCity") String locationAddressCity,
                                                                                          @Param("gradeLow") Double gradeLow,
                                                                                          @Param("gradeHigh") Double gradeHigh,
                                                                                          @Param("latitude") Double latitude,
                                                                                          @Param("longitude") Double longitude,
                                                                                          @Param("distance") Double distance,
                                                                                          @Param("dateTime") LocalDateTime dateTime,
                                                                                          @Param("employeeType") EmployeeType employeeType,
                                                                                          @Param("appointmentStatus") AppointmentStatus appointmentStatus,
                                                                                          Pageable pageable);


    @Query("select distinct ph from Pharmacy ph left join ph.complaints where ph.active = true and ph.id = :id")
    Optional<Pharmacy> getPharmacyWithComplaints(@Param("id") Long id);

    @Query("select ph from Pharmacy ph where ph.active = true and ph in (select ph from Patient p join p.subscribedTo ph where p.id = :id and p.active = true)")
    Page<Pharmacy> getSubscriptionsForPatient(@Param("id") Long id, Pageable pageable);

    @Query("select p from Pharmacy p left join fetch p.promotionSubscribers where p.active=true and p.id=:id")
    Optional<Pharmacy> findOneWithSubscribers(@Param("id") Long pharmacyId);
}
