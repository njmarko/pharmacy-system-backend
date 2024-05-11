package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.LeaveDaysRequest;
import com.mrsisa.pharmacy.domain.enums.LeaveDaysRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ILeaveDaysRequestRepository extends JpaRepository<LeaveDaysRequest, Long> {
    @Query("select req from LeaveDaysRequest req where req.active=true and req.employee.active=true " +
            "and exists(select ec from EmploymentContract ec where ec.pharmacyEmployee.id=req.employee.id and ec.active=true and ec.pharmacy.id=:id)" +
            " and req.from >= current_date and req.leaveDaysRequestStatus=0 and req.employee.employeeType=0")
    Page<LeaveDaysRequest> getPendingPharmacistsRequest(@Param("id") Long pharmacyId, Pageable pageable);

    @Query("select req from LeaveDaysRequest req where req.active=true and req.employee.active=true" +
            " and req.from >= current_date and req.leaveDaysRequestStatus=0 and req.employee.employeeType=1")
    Page<LeaveDaysRequest> getPendingDermatologistsRequests(Pageable pageable);

    @Query("select req from LeaveDaysRequest req where req.active=true and req.employee.active=true and req.employee.id=:id " +
            "and exists(select ec from EmploymentContract ec where ec.pharmacyEmployee.id=req.employee.id and ec.active=true)" +
            " and req.from >= current_date and req.leaveDaysRequestStatus=0")
    List<LeaveDaysRequest> getAllPendingLeaveDaysRequestForEmployee(@Param("id") Long employeeId);

    @Query("select req from LeaveDaysRequest req where req.active=true and req.employee.active=true and req.employee.id=:id " +
            "and exists(select ec from EmploymentContract ec where ec.pharmacyEmployee.id=req.employee.id and ec.active=true)" +
            " and (req.leaveDaysRequestStatus=0 or req.leaveDaysRequestStatus=1) " +
            "and ((req.from >= :from and req.from <= :to) or (req.to >= :from and req.to <= :to))")
    List<LeaveDaysRequest> getAllPendingAndAcceptedLeaveDaysRequestForEmployeeForRange(@Param("id") Long employeeId,
                                                                                       @Param("from") LocalDate from,
                                                                                       @Param("to") LocalDate to);

    @Query("select req from LeaveDaysRequest req where req.active=true and req.employee.active=true and req.employee.id=:id " +
            "and exists(select ec from EmploymentContract ec where ec.pharmacyEmployee.id=req.employee.id and ec.active=true)" +
            " and (req.leaveDaysRequestStatus=0 or req.leaveDaysRequestStatus=1)")
    List<LeaveDaysRequest> getAllPendingAndAcceptedLeaveDaysRequestForEmployee(@Param("id") Long employeeId);

    @Query("select req from LeaveDaysRequest req where req.active=true and req.employee.id=:employeeId" +
            " and req.from <= :theDate and req.to >= :theDate and req.leaveDaysRequestStatus=:status")
    Stream<LeaveDaysRequest> findForEmployeeContainingDate(@Param("employeeId") Long employeeId,
                                                           @Param("theDate") LocalDate theDate,
                                                           @Param("status") LeaveDaysRequestStatus status);

    @Query("select req from LeaveDaysRequest req where req.active=true and req.leaveDaysRequestStatus=:status and req.from < :today")
    Stream<LeaveDaysRequest> getStartedPendingRequestsStream(@Param("status") LeaveDaysRequestStatus status,
                                                             @Param("today") LocalDate today);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select req from LeaveDaysRequest req where req.id=:requestId and req.active=true")
    Optional<LeaveDaysRequest> getForUpdate(@Param("requestId") Long requestId);
}
