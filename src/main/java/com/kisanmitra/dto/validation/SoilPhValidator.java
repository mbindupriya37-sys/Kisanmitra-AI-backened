package com.kisanmitra.dto.validation;

import com.kisanmitra.dto.CropRecommendationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SoilPhValidator implements ConstraintValidator<ValidSoilPh, CropRecommendationRequestDto> {

    @Override
    public boolean isValid(CropRecommendationRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        Boolean isUnknown = dto.getSoilPhUnknown();
        if (Boolean.TRUE.equals(isUnknown)) {
            return true;
        }

        Double ph = dto.getSoilPh();
        if (ph == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Soil pH is required when soil pH is not marked as unknown")
                   .addPropertyNode("soilPh")
                   .addConstraintViolation();
            return false;
        }

        if (ph < 4.0 || ph > 9.5) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Soil pH must be between 4.0 and 9.5")
                   .addPropertyNode("soilPh")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
