package tu.somelier.cellar.domain.port.in;

import tu.somelier.cellar.domain.model.CellarEntry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface AddCellarEntryUseCase {

    CellarEntry addEntry(AddEntryCommand command);

    record AddEntryCommand(
            UUID userId,
            UUID wineId,
            int quantity,
            LocalDate purchaseDate,
            BigDecimal purchasePrice,
            String location,
            LocalDate drinkFrom,
            LocalDate drinkUntil,
            String personalNotes
    ) {}
}
