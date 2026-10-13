package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.CropRecommendationRequestRepository;
import com.kisanmitra.repository.CropRequirementRepository;
import com.kisanmitra.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropRecommendationService {

    private static final String DISCLAIMER_TEXT = "Recommended based on currently available crop requirements and your supplied conditions. Consider local expert advice before making high-cost farming decisions.";

    private final CropRecommendationRequestRepository cropRecommendationRequestRepository;
    private final UserRepository userRepository;
    private final CropRequirementRepository cropRequirementRepository;
    private final CropRecommendationEngine cropRecommendationEngine;

    public CropRecommendationService(CropRecommendationRequestRepository cropRecommendationRequestRepository,
                                      UserRepository userRepository,
                                      CropRequirementRepository cropRequirementRepository,
                                      CropRecommendationEngine cropRecommendationEngine) {
        this.cropRecommendationRequestRepository = cropRecommendationRequestRepository;
        this.userRepository = userRepository;
        this.cropRequirementRepository = cropRequirementRepository;
        this.cropRecommendationEngine = cropRecommendationEngine;
    }

    @Transactional
    public CropRecommendationResponseDto createRequest(CropRecommendationRequestDto dto) {
        User farmer = getAuthenticatedFarmer();

        CropRecommendationRequest request = new CropRecommendationRequest();
        request.setFarmer(farmer);
        request.setPincode(dto.getPincode() != null && !dto.getPincode().trim().isEmpty() ? dto.getPincode().trim() : null);
        request.setState(dto.getState() != null ? dto.getState().trim() : null);
        request.setDistrict(dto.getDistrict() != null ? dto.getDistrict().trim() : null);
        request.setVillage(dto.getVillage() != null && !dto.getVillage().trim().isEmpty() ? dto.getVillage().trim() : null);
        request.setSoilType(dto.getSoilType());
        request.setSoilPh(Boolean.TRUE.equals(dto.getSoilPhUnknown()) ? null : dto.getSoilPh());
        request.setSoilPhUnknown(dto.getSoilPhUnknown() != null ? dto.getSoilPhUnknown() : false);
        request.setWaterAvailability(dto.getWaterAvailability());
        request.setIrrigation(dto.getIrrigation());
        request.setExpectedRainfall(dto.getExpectedRainfall());
        request.setSeason(dto.getSeason());
        request.setTemperatureRange(dto.getTemperatureRange());
        request.setFarmSize(dto.getFarmSize());

        if (dto.getPriorities() != null) {
            request.setPriorities(new HashSet<>(dto.getPriorities()));
        }

        request.setStatus(RecommendationStatus.PENDING);

        CropRecommendationRequest saved = cropRecommendationRequestRepository.save(request);

        return CropRecommendationResponseDto.fromEntity(saved, "Crop recommendation request submitted successfully");
    }

    @Transactional(readOnly = true)
    public PageResponse<CropRecommendationResponseDto> getFarmerRequests(int page, int size) {
        User farmer = getAuthenticatedFarmer();

        int normalizedPage = Math.max(0, page);
        int normalizedSize = size <= 0 ? 5 : Math.min(20, size);

        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CropRecommendationRequest> springPage = cropRecommendationRequestRepository.findByFarmerId(farmer.getId(), pageable);

        List<CropRecommendationResponseDto> content = springPage.getContent().stream()
                .map(req -> CropRecommendationResponseDto.fromEntity(req, null))
                .collect(Collectors.toList());

        return PageResponse.fromPage(springPage, content);
    }

    @Transactional
    public CropRecommendationResultListDto getRecommendationResults(Long requestId) {
        User farmer = getAuthenticatedFarmer();

        CropRecommendationRequest request = cropRecommendationRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop recommendation request not found with id: " + requestId));

        if (!request.getFarmer().getId().equals(farmer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Crop recommendation request does not belong to the authenticated farmer");
        }

        try {
            List<CropRequirement> activeRequirements = cropRequirementRepository.findAllActiveWithCrop();
            List<CropRecommendationResultDto> recommendations = cropRecommendationEngine.evaluateAllCrops(request, activeRequirements);

            request.setStatus(RecommendationStatus.COMPLETED);
            cropRecommendationRequestRepository.save(request);

            CropRecommendationResultListDto resultDto = new CropRecommendationResultListDto(
                    request.getId(),
                    request.getStatus(),
                    request.getCreatedAt(),
                    recommendations,
                    DISCLAIMER_TEXT
            );
            resultDto.setPincode(request.getPincode());
            resultDto.setState(request.getState());
            resultDto.setDistrict(request.getDistrict());
            resultDto.setVillage(request.getVillage());
            resultDto.setLocality(request.getVillage());
            resultDto.setSoilType(request.getSoilType());
            resultDto.setSeason(request.getSeason());
            resultDto.setFarmSize(request.getFarmSize());

            return resultDto;
        } catch (Exception ex) {
            request.setStatus(RecommendationStatus.FAILED);
            cropRecommendationRequestRepository.save(request);
            if (ex instanceof ResponseStatusException) {
                throw ex;
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to compute crop recommendations: " + ex.getMessage(), ex);
        }
    }

    private User getAuthenticatedFarmer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        String email = authentication.getName();
        User farmer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer profile not found for user: " + email));

        if (farmer.getRole() != Role.FARMER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only users with FARMER role can perform crop recommendation actions");
        }

        return farmer;
    }
}
