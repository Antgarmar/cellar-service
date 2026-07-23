package tu.somelier.cellar.infrastructure.adapter.out.persistence;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import tu.somelier.cellar.domain.model.CellarEntry;
import tu.somelier.cellar.domain.model.CellarEntryStatus;
import tu.somelier.cellar.domain.port.out.CellarEntryRepositoryPort;
import tu.somelier.cellar.infrastructure.adapter.out.persistence.entity.CellarEntryEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CellarEntryRepositoryAdapter implements CellarEntryRepositoryPort {

    private final JpaCellarEntryRepository jpaRepository;

    @Override
    public CellarEntry save(CellarEntry entry) {
        return toDomain(jpaRepository.save(toEntity(entry)));
    }

    @Override
    public Optional<CellarEntry> findByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.findByIdAndUserId(id, userId).map(this::toDomain);
    }

    @Override
    public Page<CellarEntry> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable).map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteByIdAndUserId(UUID id, UUID userId) {
        jpaRepository.deleteByIdAndUserId(id, userId);
    }

    @Override
    public List<CellarEntry> findAvailableByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndStatus(userId, CellarEntryStatus.AVAILABLE)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<CellarEntry> findDrinkNow(UUID userId, LocalDate today) {
        return jpaRepository.findDrinkNow(userId, today).stream().map(this::toDomain).toList();
    }

    @Override
    public List<CellarEntry> findExpiringSoon(UUID userId, LocalDate today, LocalDate until) {
        return jpaRepository.findExpiringSoon(userId, today, until).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.existsByIdAndUserId(id, userId);
    }

    // -------------------------------------------------------------------------

    private CellarEntry toDomain(CellarEntryEntity e) {
        return CellarEntry.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .wineId(e.getWineId())
                .quantity(e.getQuantity())
                .purchaseDate(e.getPurchaseDate())
                .purchasePrice(e.getPurchasePrice())
                .location(e.getLocation())
                .drinkFrom(e.getDrinkFrom())
                .drinkUntil(e.getDrinkUntil())
                .personalNotes(e.getPersonalNotes())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private CellarEntryEntity toEntity(CellarEntry d) {
        return CellarEntryEntity.builder()
                .id(d.getId())
                .userId(d.getUserId())
                .wineId(d.getWineId())
                .quantity(d.getQuantity())
                .purchaseDate(d.getPurchaseDate())
                .purchasePrice(d.getPurchasePrice())
                .location(d.getLocation())
                .drinkFrom(d.getDrinkFrom())
                .drinkUntil(d.getDrinkUntil())
                .personalNotes(d.getPersonalNotes())
                .status(d.getStatus())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
