package com.kisanmitra.repository;

import com.kisanmitra.entity.PublicBufferStockObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PublicBufferStockRepository extends JpaRepository<PublicBufferStockObservation, Long> {

    @Query("SELECT b FROM PublicBufferStockObservation b " +
           "WHERE UPPER(b.commodity) = UPPER(:commodity) " +
           "ORDER BY b.observationDate DESC")
    List<PublicBufferStockObservation> findByCommodityOrderByDateDesc(@Param("commodity") String commodity);

    @Query("SELECT b FROM PublicBufferStockObservation b " +
           "WHERE UPPER(b.commodity) = UPPER(:commodity) " +
           "  AND b.observationDate = :date")
    Optional<PublicBufferStockObservation> findByCommodityAndDate(
            @Param("commodity") String commodity,
            @Param("date") LocalDate date
    );
}
