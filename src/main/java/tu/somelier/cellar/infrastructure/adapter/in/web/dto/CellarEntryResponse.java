package tu.somelier.cellar.infrastructure.adapter.in.web.dto;

import tu.somelier.cellar.domain.model.CellarEntry;
import tu.somelier.cellar.domain.model.CellarEntryStatus;
import tu.somelier.cellar.domain.model.WineDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CellarEntryResponse(
        UUID id,
        UUID userId,
        UUID wineId,
        WineDetails wineDetails,
        int quantity,
        LocalDate purchaseDate,
        BigDecimal purchasePrice,
        String location,
        LocalDate drinkFrom,
        LocalDate drinkUntil,
        String personalNotes,
        CellarEntryStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CellarEntryResponse from(CellarEntry entry) {
        return new CellarEntryResponse(
                entry.getId(),
                entry.getUserId(),
                entry.getWineId(),
                entry.getWineDetails(),
                entry.getQuantity(),
                entry.getPurchaseDate(),
                entry.getPurchasePrice(),
                entry.getLocation(),
                entry.getDrinkFrom(),
                entry.getDrinkUntil(),
                entry.getPersonalNotes(),
                entry.getStatus(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}
