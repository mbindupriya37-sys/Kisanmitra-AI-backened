package com.kisanmitra.repository;

import com.kisanmitra.entity.CropDiseaseAssessmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropDiseaseAssessmentLogRepository extends JpaRepository<CropDiseaseAssessmentLog, Long> {

    List<CropDiseaseAssessmentLog> findByFarmerEmailOrderByCreatedAtDesc(String farmerEmail);

    List<CropDiseaseAssessmentLog> findByKvkConsultationRecommendedTrueOrderByCreatedAtDesc();
}
