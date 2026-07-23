package tu.somelier.cellar.domain.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tu.somelier.cellar.domain.model.CellarEntry;

import java.util.UUID;

public interface GetCellarUseCase {

    Page<CellarEntry> getCellar(UUID userId, Pageable pageable);
}
