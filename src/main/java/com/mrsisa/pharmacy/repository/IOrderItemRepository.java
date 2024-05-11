package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.valueobjects.MedicineOrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface IOrderItemRepository extends JpaRepository<MedicineOrderInfo, Long> {

    @Query("select item from MedicineOrderInfo item where item.active=true and item.order.id=:orderId and item.medicine.id=:medicineId")
    Optional<MedicineOrderInfo> getItemWithMedicine(@Param("orderId") Long orderId,
                                                    @Param("medicineId") Long medicineId);

    @Query("select item from MedicineOrderInfo item where item.active=true and item.order.id=:orderId and item.id=:itemId")
    Optional<MedicineOrderInfo> findItemForOrder(@Param("orderId") Long orderId,
                                                 @Param("itemId") Long itemId);

    @Query("select item from MedicineOrderInfo item where item.active=true and item.order.id=:orderId" +
            " and lower(item.medicine.name) like concat('%',lower(:name),'%') ")
    Page<MedicineOrderInfo> getItemsForOrder(@Param("orderId") Long orderId,
                                             @Param("name") String name,
                                             Pageable pageable);

    @Query("select item from MedicineOrderInfo item where item.active=true and item.order.id=:orderId")
    Stream<MedicineOrderInfo> getItemsForOrderStream(@Param("orderId") Long orderId);

    @Query("select item from MedicineOrderInfo item where item.active=true and item.medicine.id=:medicineId and item.order.active=true" +
            " and item.order.pharmacy.id=:pharmacyId and item.order.orderStatus<>1")
    Stream<MedicineOrderInfo> getItemsWithMedicine(@Param("pharmacyId") Long pharmacyId,
                                                   @Param("medicineId") Long medicineId);
}
