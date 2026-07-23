package tu.somelier.cellar.domain.port.out;

import tu.somelier.cellar.domain.model.CellarStats;

import java.util.Optional;
import java.util.UUID;

public interface CellarStatsCachePort {

    Optional<CellarStats> getStats(UUID userId);

    void saveStats(UUID userId, CellarStats stats);

    void evictStats(UUID userId);
}
