package com.kisanmitra.repository;

import com.kisanmitra.entity.CropRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRequirementRepository extends JpaRepository<CropRequirement, Long> {

    Optional<CropRequirement> findByCropId(Long cropId);

    Optional<CropRequirement> findByCropNameIgnoreCase(String name);

    List<CropRequirement> findByActiveTrue();

    @org.springframework.data.jpa.repository.Query("SELECT cr FROM CropRequirement cr JOIN FETCH cr.crop WHERE cr.active = true")
    List<CropRequirement> findAllActiveWithCrop();
}
