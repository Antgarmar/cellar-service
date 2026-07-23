package tu.somelier.cellar.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CellarEntry {

    private UUID id;
    private UUID userId;
    private UUID wineId;
    private int quantity;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private String location;
    private LocalDate drinkFrom;
    private LocalDate drinkUntil;
    private String personalNotes;
    private CellarEntryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Enriched from wine-service; not persisted. */
    private WineDetails wineDetails;

    public boolean isReadyToDrink(LocalDate today) {
        return status == CellarEntryStatus.AVAILABLE
                && (drinkFrom == null || !today.isBefore(drinkFrom))
                && (drinkUntil == null || !today.isAfter(drinkUntil));
    }

    public boolean isExpiringSoon(LocalDate today, int days) {
        return status == CellarEntryStatus.AVAILABLE
                && drinkUntil != null
                && !drinkUntil.isBefore(today)
                && !drinkUntil.isAfter(today.plusDays(days));
    }
}
