package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.Offer;
import com.mrsisa.pharmacy.domain.enums.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface IOfferRepository extends JpaRepository<Offer, Long> {


    @Query("select o from Offer o where o.supplier.id = :supplierId and o.originalOrder.id = :orderId and o.active = true")
    Optional<Offer> getOfferForSupplierAndOrder(@Param("supplierId") Long supplierId,
                                                @Param("orderId") Long orderId);

    @Query("select o from Offer o where o.supplier.id = :supplierId and o.id = :offerId and o.active = true ")
    Optional<Offer> getOfferForSupplierUsingOfferId(@Param("supplierId") Long supplierId,
                                                    @Param("offerId") Long offerId);

    @Query(value = "select o from Offer o where o.active = true and o.supplier.id = :supplierId and (o.offerStatus = :status or :status is null )")
    Page<Offer> getOfferForSupplierAndStatus(@Param("supplierId") Long supplier, @Param("status") OfferStatus status, Pageable pageable);

    @Query("select o from Offer o where o.active=true and o.originalOrder.id=:id and (lower(o.supplier.company) like concat('%', :name, '%') or :name is null )")
    Page<Offer> getOffersForOrder(@Param("id") Long orderId, @Param("name") String query, Pageable pageable);

    @Query("select o from Offer o where o.active=true and o.originalOrder.id=:orderId and o.id=:offerId")
    Optional<Offer> findOfferForOrder(@Param("offerId") Long offerId, @Param("orderId") Long orderId);

    @Query("select o from Offer o where o.active=true and o.originalOrder.id=:orderId")
    Stream<Offer> getOrderOffersStream(@Param("orderId") Long orderId);

    @Query("select count(o) from Offer o where o.active=true and o.originalOrder.id=:orderId")
    Optional<Long> getOfferCountForOrder(@Param("orderId") Long orderId);
}
