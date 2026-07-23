package tu.somelier.cellar.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tu.somelier.cellar.domain.model.CellarEntryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cellar_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CellarEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "wine_id", nullable = false)
    private UUID wineId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(length = 100)
    private String location;

    @Column(name = "drink_from")
    private LocalDate drinkFrom;

    @Column(name = "drink_until")
    private LocalDate drinkUntil;

    @Column(name = "personal_notes", columnDefinition = "TEXT")
    private String personalNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CellarEntryStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
