package tu.somelier.cellar.domain.port.out;

import tu.somelier.cellar.domain.model.WineDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WineServicePort {

    Optional<WineDetails> findWineById(UUID wineId);

    Map<UUID, WineDetails> findWinesByIds(Collection<UUID> wineIds);
}
