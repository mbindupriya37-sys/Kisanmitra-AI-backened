package com.kisanmitra.repository;

import com.kisanmitra.entity.CropDiseaseKnowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropDiseaseKnowledgeRepository extends JpaRepository<CropDiseaseKnowledge, Long> {

    Optional<CropDiseaseKnowledge> findByCropNameIgnoreCaseAndDiseaseNameIgnoreCase(String cropName, String diseaseName);

    List<CropDiseaseKnowledge> findByCropNameIgnoreCase(String cropName);

    @Query("SELECT d FROM CropDiseaseKnowledge d WHERE " +
           "(:cropName IS NULL OR LOWER(d.cropName) = LOWER(:cropName)) AND " +
           "(:keyword IS NULL OR LOWER(d.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.scientificName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.symptomsDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.affectedPlantParts) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<CropDiseaseKnowledge> searchKnowledge(@Param("cropName") String cropName,
                                                @Param("keyword") String keyword);
}
