package com.mrsisa.pharmacy.repository;

import com.mrsisa.pharmacy.domain.entities.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IStockPriceRepository extends JpaRepository<StockPrice, Long> {
    @Query("select sp from StockPrice sp where sp.medicineStock.id=:id and sp.to is null")
    Optional<StockPrice> findActiveForStock(@Param("id") Long stockId);
}
