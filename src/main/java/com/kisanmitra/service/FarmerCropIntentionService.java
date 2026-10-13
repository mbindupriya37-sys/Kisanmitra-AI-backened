package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.*;
import com.kisanmitra.repository.CropRepository;
import com.kisanmitra.repository.FarmerCropIntentionRepository;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FarmerCropIntentionService {

    private final FarmerCropIntentionRepository farmerCropIntentionRepository;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;

    public FarmerCropIntentionService(FarmerCropIntentionRepository farmerCropIntentionRepository,
                                      CropRepository cropRepository,
                                      UserRepository userRepository) {
        this.farmerCropIntentionRepository = farmerCropIntentionRepository;
        this.cropRepository = cropRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FarmerCropIntentionResponseDto createIntention(FarmerCropIntentionRequestDto dto) {
        User farmer = getAuthenticatedFarmer();

        String normState = dto.getState() != null ? dto.getState().trim() : "";
        String normDistrict = dto.getDistrict() != null ? dto.getDistrict().trim() : "";

        Crop crop = cropRepository.findById(dto.getCropId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop not found with ID: " + dto.getCropId()));

        boolean duplicateExists = farmerCropIntentionRepository.existsDuplicateIntention(
                farmer.getId(), dto.getCropId(), normState, normDistrict, dto.getIntendedSeason());
        if (duplicateExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You have already recorded an intention for " + crop.getName() + " in " + normDistrict + ", " + normState + " for " + dto.getIntendedSeason() + " season. You can edit your existing intention instead of creating a duplicate.");
        }

        FarmerCropIntention intention = new FarmerCropIntention();
        intention.setFarmer(farmer);
        intention.setState(normState);
        intention.setDistrict(normDistrict);
        intention.setCrop(crop);
        intention.setIntendedSeason(dto.getIntendedSeason());
        intention.setFarmSize(dto.getFarmSize());

        FarmerCropIntention saved = farmerCropIntentionRepository.save(intention);
        return FarmerCropIntentionResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<FarmerCropIntentionResponseDto> getFarmerIntentions(int page, int size) {
        User farmer = getAuthenticatedFarmer();

        int normalizedPage = Math.max(0, page);
        int normalizedSize = size <= 0 ? 5 : Math.min(20, size);

        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FarmerCropIntention> springPage = farmerCropIntentionRepository.findByFarmerId(farmer.getId(), pageable);

        List<FarmerCropIntentionResponseDto> content = springPage.getContent().stream()
                .map(FarmerCropIntentionResponseDto::fromEntity)
                .collect(Collectors.toList());

        return PageResponse.fromPage(springPage, content);
    }

    @Transactional
    public FarmerCropIntentionResponseDto updateIntention(Long id, FarmerCropIntentionRequestDto dto) {
        User farmer = getAuthenticatedFarmer();

        FarmerCropIntention intention = farmerCropIntentionRepository.findByIdAndFarmerId(id, farmer.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop intention record not found or not owned by you. ID: " + id));

        Crop crop = cropRepository.findById(dto.getCropId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop not found with ID: " + dto.getCropId()));

        String normState = dto.getState() != null ? dto.getState().trim() : "";
        String normDistrict = dto.getDistrict() != null ? dto.getDistrict().trim() : "";

        boolean duplicateExists = farmerCropIntentionRepository.existsDuplicateIntentionExcludingId(
                farmer.getId(), dto.getCropId(), normState, normDistrict, dto.getIntendedSeason(), id);
        if (duplicateExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Another intention for " + crop.getName() + " in " + normDistrict + ", " + normState + " for " + dto.getIntendedSeason() + " season already exists. You cannot update this record into a duplicate.");
        }

        intention.setState(normState);
        intention.setDistrict(normDistrict);
        intention.setCrop(crop);
        intention.setIntendedSeason(dto.getIntendedSeason());
        intention.setFarmSize(dto.getFarmSize());

        FarmerCropIntention updated = farmerCropIntentionRepository.save(intention);
        return FarmerCropIntentionResponseDto.fromEntity(updated);
    }

    @Transactional
    public void deleteIntention(Long id) {
        User farmer = getAuthenticatedFarmer();

        FarmerCropIntention intention = farmerCropIntentionRepository.findByIdAndFarmerId(id, farmer.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crop intention record not found or not owned by you. ID: " + id));

        farmerCropIntentionRepository.delete(intention);
    }

    @Transactional(readOnly = true)
    public OversupplyRiskResponseDto getOversupplyRisk(String state, String district, Season season) {
        getAuthenticatedFarmer();

        String queryState = (state != null && !state.isBlank()) ? state.trim() : "Andhra Pradesh";
        String queryDistrict = (district != null && !district.isBlank()) ? district.trim() : "Guntur";
        Season querySeason = season != null ? season : Season.KHARIF;

        List<FarmerCropIntention> regionalIntentions = farmerCropIntentionRepository
                .findRegionalIntentions(queryState, queryDistrict, querySeason);

        int totalIntentions = regionalIntentions.size();
        long distinctFarmerCount = regionalIntentions.stream()
                .map(FarmerCropIntention::getFarmer)
                .filter(Objects::nonNull)
                .map(User::getId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        int totalDistinctFarmers = (int) distinctFarmerCount;

        double totalArea = regionalIntentions.stream()
                .mapToDouble(i -> i.getFarmSize() != null ? i.getFarmSize() : 0.0)
                .sum();

        // Sample-size rule: Minimum 3 distinct farmers required in region to evaluate risk indicator
        boolean isDataSufficient = totalDistinctFarmers >= 3;

        Map<Long, List<FarmerCropIntention>> cropGroupMap = regionalIntentions.stream()
                .filter(i -> i.getCrop() != null)
                .collect(Collectors.groupingBy(i -> i.getCrop().getId()));

        List<Crop> activeCrops = cropRepository.findByActiveTrue();
        List<OversupplyRiskItemDto> items = new ArrayList<>();

        for (Crop crop : activeCrops) {
            List<FarmerCropIntention> cropIntentions = cropGroupMap.getOrDefault(crop.getId(), Collections.emptyList());
            int cropCount = cropIntentions.size();
            double cropArea = cropIntentions.stream()
                    .mapToDouble(i -> i.getFarmSize() != null ? i.getFarmSize() : 0.0)
                    .sum();

            double intentionShare = totalIntentions > 0 ? ((double) cropCount / totalIntentions) * 100.0 : 0.0;
            double areaShare = totalArea > 0 ? (cropArea / totalArea) * 100.0 : 0.0;

            String riskLevel;
            String riskLabel;
            String explanation;

            if (!isDataSufficient) {
                riskLevel = "INSUFFICIENT_DATA";
                riskLabel = "Insufficient Data";
                explanation = "Sample size is limited (" + totalDistinctFarmers + " distinct " + (totalDistinctFarmers == 1 ? "farmer" : "farmers") + ", " + totalIntentions + " total " + (totalIntentions == 1 ? "intention" : "intentions") + " in this region). At least 3 distinct farmers are required for indicative risk evaluation. These software indicators are sample-based and not agronomically validated.";
            } else {
                // Software indicator thresholds:
                // intentionShare >= 45% or areaShare >= 45% -> HIGH
                // intentionShare >= 25% or areaShare >= 25% -> MODERATE
                // < 25% -> LOW
                double maxShare = Math.max(intentionShare, areaShare);
                if (maxShare >= 45.0) {
                    riskLevel = "HIGH";
                    riskLabel = "High Oversupply Risk";
                    explanation = "Farmer intentions for " + crop.getName() + " represent a relatively large share (" + String.format("%.1f", maxShare) + "%) of recorded intentions in " + queryDistrict + ", " + queryState + " for " + querySeason + " season. This indicative software signal suggests potential oversupply risk if similar production materializes (not agronomically validated).";
                } else if (maxShare >= 25.0) {
                    riskLevel = "MODERATE";
                    riskLabel = "Moderate Oversupply Risk";
                    explanation = "Current recorded farmer intentions for " + crop.getName() + " account for a moderate concentration (" + String.format("%.1f", maxShare) + "%) of recorded intentions in this region. This indicative software signal represents sample concentration, not an agronomically validated yield forecast.";
                } else {
                    riskLevel = "LOW";
                    riskLabel = "Low Oversupply Risk";
                    explanation = "Recorded farmer intentions for " + crop.getName() + " are currently distributed across multiple crops (" + String.format("%.1f", maxShare) + "% share), indicating lower concentration among the " + totalDistinctFarmers + " reporting farmers.";
                }
            }

            items.add(new OversupplyRiskItemDto(
                    crop.getId(),
                    crop.getName(),
                    crop.getScientificName(),
                    queryState,
                    queryDistrict,
                    querySeason,
                    cropCount,
                    Math.round(cropArea * 10.0) / 10.0,
                    Math.round(intentionShare * 10.0) / 10.0,
                    Math.round(areaShare * 10.0) / 10.0,
                    riskLevel,
                    riskLabel,
                    explanation,
                    isDataSufficient
            ));
        }

        items.sort((a, b) -> Double.compare(b.getIntentionShare(), a.getIntentionShare()));

        String summaryMsg = isDataSufficient
                ? "Aggregated " + totalIntentions + " crop intention " + (totalIntentions == 1 ? "record" : "records") + " from " + totalDistinctFarmers + " distinct farmers (" + String.format("%.1f", totalArea) + " total acres) in " + queryDistrict + ", " + queryState + " for " + querySeason + " season."
                : "Not enough distinct farmer participation (" + totalDistinctFarmers + " distinct " + (totalDistinctFarmers == 1 ? "farmer" : "farmers") + ", " + totalIntentions + " total " + (totalIntentions == 1 ? "record" : "records") + ") in " + queryDistrict + ", " + queryState + " for " + querySeason + " season. A minimum of 3 distinct farmers is required to evaluate indicative oversupply risk.";

        return new OversupplyRiskResponseDto(
                queryState,
                queryDistrict,
                querySeason,
                totalIntentions,
                totalDistinctFarmers,
                Math.round(totalArea * 10.0) / 10.0,
                isDataSufficient,
                summaryMsg,
                items,
                LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public List<CropSummaryDto> getActiveCrops() {
        getAuthenticatedFarmer(); // Ensure authenticated FARMER
        return cropRepository.findByActiveTrue().stream()
                .map(CropSummaryDto::fromEntity)
                .collect(Collectors.toList());
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only users with FARMER role can perform crop intention actions");
        }

        return farmer;
    }
}
