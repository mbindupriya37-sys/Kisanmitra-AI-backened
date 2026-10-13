package com.kisanmitra.repository;

import com.kisanmitra.entity.PublicProcurementObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PublicProcurementRepository extends JpaRepository<PublicProcurementObservation, Long> {

    @Query("SELECT p FROM PublicProcurementObservation p " +
           "WHERE UPPER(p.commodity) = UPPER(:commodity) " +
           "ORDER BY p.reportingAsOfDate DESC")
    List<PublicProcurementObservation> findLatestByCommodity(@Param("commodity") String commodity);

    @Query("SELECT p FROM PublicProcurementObservation p " +
           "WHERE UPPER(p.commodity) = UPPER(:commodity) " +
           "  AND UPPER(p.marketingSeasonType) = UPPER(:seasonType) " +
           "  AND p.marketingSeasonYear = :seasonYear " +
           "  AND UPPER(p.stateName) = UPPER(:stateName) " +
           "ORDER BY p.reportingAsOfDate DESC")
    List<PublicProcurementObservation> findBySeasonAndState(
            @Param("commodity") String commodity,
            @Param("seasonType") String seasonType,
            @Param("seasonYear") String seasonYear,
            @Param("stateName") String stateName
    );

    @Query("SELECT p FROM PublicProcurementObservation p " +
           "WHERE UPPER(p.commodity) = UPPER(:commodity) " +
           "  AND UPPER(p.marketingSeasonType) = UPPER(:seasonType) " +
           "  AND p.marketingSeasonYear = :seasonYear " +
           "  AND UPPER(p.stateName) = UPPER(:stateName) " +
           "  AND p.reportingAsOfDate = :asOfDate")
    Optional<PublicProcurementObservation> findBySnapshotKey(
            @Param("commodity") String commodity,
            @Param("seasonType") String seasonType,
            @Param("seasonYear") String seasonYear,
            @Param("stateName") String stateName,
            @Param("asOfDate") LocalDate asOfDate
    );
}
