package tu.somelier.cellar.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionLog {

    private UUID id;
    private UUID entryId;
    private UUID userId;
    private UUID wineId;
    private int quantity;
    private LocalDateTime consumedAt;
    private String occasion;
}
