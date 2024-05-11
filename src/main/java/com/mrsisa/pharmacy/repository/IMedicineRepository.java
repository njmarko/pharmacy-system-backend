package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Medicine;
import com.mrsisa.pharmacy.domain.enums.MedicineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface IMedicineRepository extends JpaRepository<Medicine, Long> {

    @Query(value = "select distinct med from Medicine med " +
            " where med.active=true and lower(med.name) like concat('%',lower(:name),'%') and " +
            "med.averageGrade >= :lowGrade and med.averageGrade <= :highGrade and (med.issueOnRecipe = :issueOnRecipe or :issueOnRecipe is null) and (med.medicineType = :type or :type is null)",
            countQuery = "select count(med) from Medicine med " +
                    " where med.active=true and lower(med.name) like concat('%',lower(:name),'%') and " +
                    "med.averageGrade >= :lowGrade and med.averageGrade <= :highGrade and (med.issueOnRecipe = :issueOnRecipe or :issueOnRecipe is null) and (med.medicineType = :type or :type is null)")
    Page<Medicine> getMedicineSearchAndFilterWithIssueOnRecipe(@Param("name") String name,
                                                               @Param("lowGrade") Double lowGrade,
                                                               @Param("highGrade") Double highGrade,
                                                               @Param("issueOnRecipe") Boolean issueOnRecipe,
                                                               @Param("type") MedicineType type,
                                                               Pageable pageable);


    Medicine findByIdAndActiveTrue(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Medicine as m where m.active = true and m.id = :id")
    Optional<Medicine> getByIdAndActiveTrue(@Param("id") Long id);

    @Query("select m from Medicine as m where m.active = true and m.id = :id")
    Optional<Medicine> getByIdAndActiveTrueUnlocked(@Param("id") Long id);


    @Query("select m from Medicine m where m.active=true and lower(m.code)=lower(:code) ")
    Optional<Medicine> getByMedicineCode(@Param("code") String medicineCode);

    List<Medicine> findAllByActiveTrue();

    // Used join for pagination instead of just p.allergicTo
    @Query("select m from Medicine as m where m.active=true and (lower(m.name) like concat('%',:name,'%') or :name is null) " +
            "and m in (select med from Patient p join p.allergicTo med where p.id=:id and med.active=true and p.active=true)")
    Page<Medicine> findPatientAllergies(@Param("id") Long id, @Param("name") String name, Pageable pageable);

    // Used join in nested query
    @Query(value = "select m from Medicine as m where m.active=true and (lower(m.name) like concat('%',:name,'%') or :name is null)" +
            "and m not in (select med from Patient p join p.allergicTo med where p.id=:id and med.active=true and p.active=true)"
    )
    Page<Medicine> findNotAllergicTo(@Param("id") Long id, @Param("name") String name, Pageable pageable);

    @Query("select m from Patient as p join p.allergicTo m where p.active = true and p.id = :id and m.active = true and m.id = :medicineId")
    Medicine findByPatientIdAndMedicineId(@Param("id") Long id, @Param("medicineId") Long medicineId);

    @Query("select m from Medicine m where m in (select med from Patient p join p.allergicTo med where p.id = :patientId and med.active=true and med.id = :medicineId)")
    Optional<Medicine> getMedicineAllergyByMedicineIdAndPatientId(@Param("medicineId") Long medicineId, @Param("patientId") Long patientId);

    @Query("select m from Medicine m where m.active=true " +
            "and lower(m.name) like concat('%',lower(:name),'%') " +
            "and m.averageGrade >= :lowGrade and m.averageGrade <= :highGrade " +
            "and (m.issueOnRecipe = :issueOnRecipe or :issueOnRecipe is null) and (m.medicineType = :type or :type is null) " +
            "and m not in (select item.medicine from MedicineOrderInfo item where item.order.id=:orderId and item.active=true)")
    Page<Medicine> findAllNotInOrder(@Param("orderId") Long orderId,
                                     @Param("name") String name,
                                     @Param("lowGrade") Double lowGrade,
                                     @Param("highGrade") Double highGrade,
                                     @Param("issueOnRecipe") Boolean issueOnRecipe,
                                     @Param("type") MedicineType type, Pageable pageable);
}
