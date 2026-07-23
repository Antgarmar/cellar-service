package tu.somelier.cellar.domain.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tu.somelier.cellar.domain.model.CellarEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CellarEntryRepositoryPort {

    CellarEntry save(CellarEntry entry);

    Optional<CellarEntry> findByIdAndUserId(UUID id, UUID userId);

    Page<CellarEntry> findByUserId(UUID userId, Pageable pageable);

    void deleteByIdAndUserId(UUID id, UUID userId);

    List<CellarEntry> findAvailableByUserId(UUID userId);

    List<CellarEntry> findDrinkNow(UUID userId, LocalDate today);

    List<CellarEntry> findExpiringSoon(UUID userId, LocalDate today, LocalDate until);

    boolean existsByIdAndUserId(UUID id, UUID userId);
}
