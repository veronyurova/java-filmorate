package ru.yandex.practicum.filmorate.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class MinDateValidator implements ConstraintValidator<MinDate, LocalDate> {
    private String date;

    @Override
    public void initialize(MinDate constraintAnnotation) {
        this.date = constraintAnnotation.date();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        Instant minDate = LocalDate.parse(date).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant providedDate = value.atStartOfDay().toInstant(ZoneOffset.UTC);
        return !providedDate.isBefore(minDate);
    }
}