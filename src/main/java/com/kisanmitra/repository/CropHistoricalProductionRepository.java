package com.kisanmitra.repository;

import com.kisanmitra.entity.CropHistoricalProductionObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropHistoricalProductionRepository extends JpaRepository<CropHistoricalProductionObservation, Long> {

    @Query("SELECT p FROM CropHistoricalProductionObservation p " +
           "WHERE p.crop.id = :cropId " +
           "  AND UPPER(p.normalizedState) = UPPER(:state) " +
           "  AND UPPER(p.normalizedDistrict) = UPPER(:district) " +
           "  AND p.isActive = true " +
           "ORDER BY p.agriculturalYear DESC, p.season ASC")
    List<CropHistoricalProductionObservation> findActiveByCropAndDistrict(
            @Param("cropId") Long cropId,
            @Param("state") String state,
            @Param("district") String district
    );

    @Query("SELECT p FROM CropHistoricalProductionObservation p " +
           "WHERE p.crop.id = :cropId " +
           "  AND UPPER(p.normalizedState) = UPPER(:state) " +
           "  AND UPPER(p.normalizedDistrict) = UPPER(:district) " +
           "  AND UPPER(p.boundaryVintage) = UPPER(:boundaryVintage) " +
           "  AND p.agriculturalYear = :year " +
           "  AND UPPER(p.season) = UPPER(:season) " +
           "  AND p.sourceRevision = :revision")
    Optional<CropHistoricalProductionObservation> findByNaturalIdentity(
            @Param("cropId") Long cropId,
            @Param("state") String state,
            @Param("district") String district,
            @Param("boundaryVintage") String boundaryVintage,
            @Param("year") String year,
            @Param("season") String season,
            @Param("revision") String revision
    );

    @Query("SELECT p FROM CropHistoricalProductionObservation p " +
           "WHERE p.crop.id = :cropId " +
           "  AND UPPER(p.normalizedState) = UPPER(:state) " +
           "  AND UPPER(p.normalizedDistrict) = UPPER(:district) " +
           "  AND UPPER(p.boundaryVintage) = UPPER(:boundaryVintage) " +
           "  AND p.agriculturalYear = :year " +
           "  AND UPPER(p.season) = UPPER(:season) " +
           "  AND p.isActive = true")
    Optional<CropHistoricalProductionObservation> findActiveByCropAndSeason(
            @Param("cropId") Long cropId,
            @Param("state") String state,
            @Param("district") String district,
            @Param("boundaryVintage") String boundaryVintage,
            @Param("year") String year,
            @Param("season") String season
    );
}
