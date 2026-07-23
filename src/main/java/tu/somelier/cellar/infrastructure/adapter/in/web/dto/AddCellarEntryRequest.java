package tu.somelier.cellar.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AddCellarEntryRequest(
        @NotNull UUID wineId,
        @NotNull @Min(1) Integer quantity,
        LocalDate purchaseDate,
        @Min(0) BigDecimal purchasePrice,
        String location,
        LocalDate drinkFrom,
        LocalDate drinkUntil,
        String personalNotes
) {}
