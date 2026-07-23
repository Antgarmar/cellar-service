package tu.somelier.cellar.domain.port.in;

import tu.somelier.cellar.domain.model.CellarStats;

import java.util.UUID;

public interface GetCellarStatsUseCase {

    CellarStats getStats(UUID userId);
}
