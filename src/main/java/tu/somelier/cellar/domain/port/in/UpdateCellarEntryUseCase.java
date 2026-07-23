package tu.somelier.cellar.domain.port.in;

import tu.somelier.cellar.domain.model.CellarEntry;
import tu.somelier.cellar.domain.model.CellarEntryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface UpdateCellarEntryUseCase {

    CellarEntry updateEntry(UUID id, UUID userId, UpdateEntryCommand command);

    record UpdateEntryCommand(
            Integer quantity,
            String location,
            LocalDate drinkFrom,
            LocalDate drinkUntil,
            String personalNotes,
            CellarEntryStatus status
    ) {}
}
