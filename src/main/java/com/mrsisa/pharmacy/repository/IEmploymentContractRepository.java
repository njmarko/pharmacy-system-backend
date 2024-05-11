package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.EmploymentContract;
import com.mrsisa.pharmacy.domain.enums.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEmploymentContractRepository extends JpaRepository<EmploymentContract, Long> {

    @Query("select ec from EmploymentContract ec where ec.active=true and ec.pharmacyEmployee.id=:employeeId" +
            " and ec.pharmacy.id=:pharmacyId and ec.pharmacyEmployee.employeeType=:employeeType")
    Optional<EmploymentContract> getEmployeeContractWithPharmacy(@Param("employeeId") Long employeeId,
                                                                 @Param("pharmacyId") Long pharmacyId,
                                                                 @Param("employeeType") EmployeeType employeeType);

    @Query("select ec from EmploymentContract ec where ec.active=true and ec.pharmacyEmployee.id=:employeeId and ec.pharmacy.id=:pharmacyId")
    Optional<EmploymentContract> getEmployeeContractWithPharmacy(@Param("employeeId") Long employeeId,
                                                                 @Param("pharmacyId") Long pharmacyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ec from EmploymentContract ec where ec.active=true and ec.pharmacyEmployee.id=:employeeId and ec.pharmacy.id=:pharmacyId")
    Optional<EmploymentContract> getEmployeeContractWithPharmacyForUpdate(@Param("employeeId") Long employeeId,
                                                                          @Param("pharmacyId") Long pharmacyId);

    @Query("select ec from EmploymentContract ec where ec.active=true and ec.pharmacyEmployee.id=:employeeId" +
            " and ec.pharmacyEmployee.employeeType=:employeeType")
    Optional<EmploymentContract> getEmployeeContractForEmployee(@Param("employeeId") Long employeeId,
                                                                @Param("employeeType") EmployeeType employeeType);

    @Query("select ec from EmploymentContract ec where ec.active=true and ec.pharmacy.id=:pharmacyId" +
            " and ec.pharmacyEmployee.employeeType=:employeeType")
    List<EmploymentContract> getPharmacyEmployees(@Param("pharmacyId") Long pharmacyId,
                                                  @Param("employeeType") EmployeeType employeeType);

    @Query("select ec from EmploymentContract ec where ec.active=true and ec.pharmacy.id=:pharmacyId")
    List<EmploymentContract> getPharmacyEmployees(@Param("pharmacyId") Long pharmacyId);

    @Query("select ec from EmploymentContract ec where ec.active=true and ec.pharmacyEmployee.id=:employeeId" +
            " and ec.pharmacy.id=:pharmacyId")
    Optional<EmploymentContract> getEmployeeContractForPharmacyAndEmployee(@Param("pharmacyId") Long pharmacyId,
                                                                           @Param("employeeId") Long employeeId);

    @Query("select ec from EmploymentContract ec where ec.active=true and ec.pharmacyEmployee.id=:id")
    List<EmploymentContract> getEmployeeContractsList(@Param("id") Long id);
}
