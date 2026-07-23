package tu.somelier.cellar.domain.port.in;

import tu.somelier.cellar.domain.model.CellarEntry;

import java.util.List;
import java.util.UUID;

public interface GetExpiringSoonUseCase {

    List<CellarEntry> getExpiringSoon(UUID userId, int days);
}
