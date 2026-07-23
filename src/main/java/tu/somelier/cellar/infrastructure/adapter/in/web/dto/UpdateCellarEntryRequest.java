package tu.somelier.cellar.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import tu.somelier.cellar.domain.model.CellarEntryStatus;

import java.time.LocalDate;

public record UpdateCellarEntryRequest(
        @Min(0) Integer quantity,
        String location,
        LocalDate drinkFrom,
        LocalDate drinkUntil,
        String personalNotes,
        CellarEntryStatus status
) {}
