package com.kisanmitra.repository;

import com.kisanmitra.entity.FarmerCropIntention;
import com.kisanmitra.entity.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerCropIntentionRepository extends JpaRepository<FarmerCropIntention, Long> {

    Page<FarmerCropIntention> findByFarmerId(Long farmerId, Pageable pageable);

    Optional<FarmerCropIntention> findByIdAndFarmerId(Long id, Long farmerId);

    List<FarmerCropIntention> findByStateIgnoreCaseAndDistrictIgnoreCaseAndIntendedSeason(String state, String district, Season intendedSeason);

    List<FarmerCropIntention> findByStateIgnoreCaseAndIntendedSeason(String state, Season intendedSeason);

    List<FarmerCropIntention> findByCropId(Long cropId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM FarmerCropIntention f " +
           "WHERE f.farmer.id = :farmerId " +
           "AND f.crop.id = :cropId " +
           "AND LOWER(TRIM(f.state)) = LOWER(TRIM(:state)) " +
           "AND LOWER(TRIM(f.district)) = LOWER(TRIM(:district)) " +
           "AND f.intendedSeason = :season")
    boolean existsDuplicateIntention(
            @Param("farmerId") Long farmerId,
            @Param("cropId") Long cropId,
            @Param("state") String state,
            @Param("district") String district,
            @Param("season") Season season
    );

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM FarmerCropIntention f " +
           "WHERE f.farmer.id = :farmerId " +
           "AND f.crop.id = :cropId " +
           "AND LOWER(TRIM(f.state)) = LOWER(TRIM(:state)) " +
           "AND LOWER(TRIM(f.district)) = LOWER(TRIM(:district)) " +
           "AND f.intendedSeason = :season " +
           "AND f.id <> :excludeId")
    boolean existsDuplicateIntentionExcludingId(
            @Param("farmerId") Long farmerId,
            @Param("cropId") Long cropId,
            @Param("state") String state,
            @Param("district") String district,
            @Param("season") Season season,
            @Param("excludeId") Long excludeId
    );

    @Query("SELECT f FROM FarmerCropIntention f " +
           "WHERE LOWER(TRIM(f.state)) = LOWER(TRIM(:state)) " +
           "AND LOWER(TRIM(f.district)) = LOWER(TRIM(:district)) " +
           "AND f.intendedSeason = :season")
    List<FarmerCropIntention> findRegionalIntentions(
            @Param("state") String state,
            @Param("district") String district,
            @Param("season") Season season
    );
}

