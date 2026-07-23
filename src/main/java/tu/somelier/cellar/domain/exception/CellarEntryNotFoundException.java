package tu.somelier.cellar.domain.exception;

import java.util.UUID;

public class CellarEntryNotFoundException extends RuntimeException {

    public CellarEntryNotFoundException(UUID id) {
        super("Cellar entry not found: " + id);
    }
}
