package tu.somelier.cellar.domain.port.in;

import tu.somelier.cellar.domain.model.ConsumptionLog;

import java.util.UUID;

public interface ConsumeBottlesUseCase {

    ConsumptionLog consume(UUID entryId, UUID userId, ConsumeCommand command);

    record ConsumeCommand(
            int quantity,
            String occasion
    ) {}
}
