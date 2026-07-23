package tu.somelier.cellar.domain.port.out;

import tu.somelier.cellar.domain.model.ConsumptionLog;

public interface ConsumptionLogRepositoryPort {

    ConsumptionLog save(ConsumptionLog log);
}
