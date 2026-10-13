package com.kisanmitra.repository;

import com.kisanmitra.entity.MspPriceNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MspPriceNotificationRepository extends JpaRepository<MspPriceNotification, Long> {

    @Query("SELECT m FROM MspPriceNotification m " +
           "WHERE m.crop.id = :cropId " +
           "  AND UPPER(m.marketingSeasonType) = UPPER(:seasonType) " +
           "  AND m.seasonYear = :seasonYear " +
           "  AND m.isActive = true " +
           "ORDER BY m.effectiveDate DESC")
    List<MspPriceNotification> findActiveByCropAndSeason(
            @Param("cropId") Long cropId,
            @Param("seasonType") String seasonType,
            @Param("seasonYear") String seasonYear
    );

    @Query("SELECT m FROM MspPriceNotification m " +
           "WHERE m.crop.id = :cropId " +
           "  AND m.isActive = true " +
           "ORDER BY m.seasonYear DESC, m.effectiveDate DESC")
    List<MspPriceNotification> findLatestActiveByCrop(@Param("cropId") Long cropId);

    @Query("SELECT m FROM MspPriceNotification m " +
           "WHERE m.crop.id = :cropId " +
           "  AND UPPER(m.commodityName) = UPPER(:commodity) " +
           "  AND UPPER(m.commodityVariety) = UPPER(:variety) " +
           "  AND UPPER(m.marketingSeasonType) = UPPER(:seasonType) " +
           "  AND m.seasonYear = :year " +
           "  AND m.gazetteNotificationRef = :gazetteRef")
    Optional<MspPriceNotification> findByStatutoryIdentity(
            @Param("cropId") Long cropId,
            @Param("commodity") String commodity,
            @Param("variety") String variety,
            @Param("seasonType") String seasonType,
            @Param("year") String year,
            @Param("gazetteRef") String gazetteRef
    );
}
