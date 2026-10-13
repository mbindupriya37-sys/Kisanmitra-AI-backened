package com.kisanmitra.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SoilPhValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSoilPh {

    String message() default "Soil pH must be between 4.0 and 9.5 when soil pH is known";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
