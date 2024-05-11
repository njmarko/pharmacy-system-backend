package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.AppointmentPrice;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAppointmentPriceRepository extends JpaRepository<AppointmentPrice, Long> {
    @Query("select ap from AppointmentPrice ap where ap.pharmacy.id=:id and ap.to is null and ap.appointmentType=:appointmentType")
    Optional<AppointmentPrice> findActiveAppointmentPriceOfType(@Param("id") Long pharmacyId,
                                                                @Param("appointmentType") EmployeeType appointmentType);

}
