package tu.somelier.cellar.domain.port.in;

import tu.somelier.cellar.domain.model.CellarEntry;

import java.util.UUID;

public interface GetCellarEntryUseCase {

    CellarEntry getEntry(UUID id, UUID userId);
}
