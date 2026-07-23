package tu.somelier.cellar.domain.port.in;

import java.util.UUID;

public interface DeleteCellarEntryUseCase {

    void deleteEntry(UUID id, UUID userId);
}
