package com.kisanmitra.repository;

import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.entity.SchemeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GovernmentSchemeRepository extends JpaRepository<GovernmentScheme, Long> {

    Optional<GovernmentScheme> findByNameIgnoreCase(String name);

    List<GovernmentScheme> findByCategory(SchemeCategory category);

    @Query("SELECT s FROM GovernmentScheme s WHERE " +
           "(:category IS NULL OR s.category = :category) AND " +
           "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.purpose) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.benefits) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.ministry) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<GovernmentScheme> searchSchemes(@Param("category") SchemeCategory category,
                                          @Param("keyword") String keyword);
}
