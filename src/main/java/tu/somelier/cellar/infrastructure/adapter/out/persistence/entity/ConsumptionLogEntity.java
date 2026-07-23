package tu.somelier.cellar.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consumption_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entry_id", nullable = false)
    private UUID entryId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "wine_id", nullable = false)
    private UUID wineId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "consumed_at", nullable = false)
    private LocalDateTime consumedAt;

    @Column(length = 100)
    private String occasion;
}
