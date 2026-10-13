package com.kisanmitra.service;

import com.kisanmitra.dto.GovernmentSchemeDto;
import com.kisanmitra.dto.GovernmentSchemeRequestDto;
import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.entity.SchemeCategory;
import com.kisanmitra.exception.ResourceNotFoundException;
import com.kisanmitra.repository.GovernmentSchemeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GovernmentSchemeService {

    private final GovernmentSchemeRepository schemeRepository;

    public GovernmentSchemeService(GovernmentSchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "governmentSchemes", key = "{#category, #keyword != null ? #keyword.trim().toLowerCase() : ''}")
    public List<GovernmentSchemeDto> searchSchemes(SchemeCategory category, String keyword) {
        String query = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return schemeRepository.searchSchemes(category, query)
                .stream()
                .map(GovernmentSchemeDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "governmentSchemeById", key = "#id")
    public GovernmentSchemeDto getSchemeById(Long id) {
        GovernmentScheme scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Government Scheme not found with id: " + id));
        return new GovernmentSchemeDto(scheme);
    }

    @Transactional
    @CacheEvict(value = {"governmentSchemes", "governmentSchemeById"}, allEntries = true)
    public GovernmentSchemeDto createScheme(GovernmentSchemeRequestDto request) {
        String verifiedDate = (request.getLastVerifiedDate() != null && !request.getLastVerifiedDate().trim().isEmpty())
                ? request.getLastVerifiedDate().trim()
                : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String status = (request.getVerificationStatus() != null && !request.getVerificationStatus().trim().isEmpty())
                ? request.getVerificationStatus().trim()
                : "VERIFIED";

        GovernmentScheme scheme = new GovernmentScheme(
                request.getName().trim(),
                request.getCategory(),
                request.getMinistry().trim(),
                request.getPurpose().trim(),
                request.getEligibleApplicants().trim(),
                request.getBenefits().trim(),
                request.getRequiredDocuments().trim(),
                request.getApplicationProcess().trim(),
                request.getOfficialPortalUrl().trim(),
                request.getSourceName().trim(),
                verifiedDate,
                status
        );

        GovernmentScheme saved = schemeRepository.save(scheme);
        return new GovernmentSchemeDto(saved);
    }

    @Transactional
    @CacheEvict(value = {"governmentSchemes", "governmentSchemeById"}, allEntries = true)
    public GovernmentSchemeDto updateScheme(Long id, GovernmentSchemeRequestDto request) {
        GovernmentScheme scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Government Scheme not found with id: " + id));

        scheme.setName(request.getName().trim());
        scheme.setCategory(request.getCategory());
        scheme.setMinistry(request.getMinistry().trim());
        scheme.setPurpose(request.getPurpose().trim());
        scheme.setEligibleApplicants(request.getEligibleApplicants().trim());
        scheme.setBenefits(request.getBenefits().trim());
        scheme.setRequiredDocuments(request.getRequiredDocuments().trim());
        scheme.setApplicationProcess(request.getApplicationProcess().trim());
        scheme.setOfficialPortalUrl(request.getOfficialPortalUrl().trim());
        scheme.setSourceName(request.getSourceName().trim());

        if (request.getLastVerifiedDate() != null && !request.getLastVerifiedDate().trim().isEmpty()) {
            scheme.setLastVerifiedDate(request.getLastVerifiedDate().trim());
        }
        if (request.getVerificationStatus() != null && !request.getVerificationStatus().trim().isEmpty()) {
            scheme.setVerificationStatus(request.getVerificationStatus().trim());
        }

        GovernmentScheme saved = schemeRepository.save(scheme);
        return new GovernmentSchemeDto(saved);
    }

    @Transactional
    @CacheEvict(value = {"governmentSchemes", "governmentSchemeById"}, allEntries = true)
    public void deleteScheme(Long id) {
        GovernmentScheme scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Government Scheme not found with id: " + id));
        schemeRepository.delete(scheme);
    }
}
