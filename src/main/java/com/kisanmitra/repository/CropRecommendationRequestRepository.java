package com.kisanmitra.repository;

import com.kisanmitra.entity.CropRecommendationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRecommendationRequestRepository extends JpaRepository<CropRecommendationRequest, Long> {

    List<CropRecommendationRequest> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);

    List<CropRecommendationRequest> findByFarmerEmailOrderByCreatedAtDesc(String email);

    Page<CropRecommendationRequest> findByFarmerId(Long farmerId, Pageable pageable);
}

