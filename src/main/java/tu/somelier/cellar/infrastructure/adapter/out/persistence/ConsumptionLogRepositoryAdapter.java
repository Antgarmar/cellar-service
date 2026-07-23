package tu.somelier.cellar.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tu.somelier.cellar.domain.model.ConsumptionLog;
import tu.somelier.cellar.domain.port.out.ConsumptionLogRepositoryPort;
import tu.somelier.cellar.infrastructure.adapter.out.persistence.entity.ConsumptionLogEntity;

@Component
@RequiredArgsConstructor
public class ConsumptionLogRepositoryAdapter implements ConsumptionLogRepositoryPort {

    private final JpaConsumptionLogRepository jpaRepository;

    @Override
    public ConsumptionLog save(ConsumptionLog log) {
        return toDomain(jpaRepository.save(toEntity(log)));
    }

    private ConsumptionLog toDomain(ConsumptionLogEntity e) {
        return ConsumptionLog.builder()
                .id(e.getId())
                .entryId(e.getEntryId())
                .userId(e.getUserId())
                .wineId(e.getWineId())
                .quantity(e.getQuantity())
                .consumedAt(e.getConsumedAt())
                .occasion(e.getOccasion())
                .build();
    }

    private ConsumptionLogEntity toEntity(ConsumptionLog d) {
        return ConsumptionLogEntity.builder()
                .id(d.getId())
                .entryId(d.getEntryId())
                .userId(d.getUserId())
                .wineId(d.getWineId())
                .quantity(d.getQuantity())
                .consumedAt(d.getConsumedAt())
                .occasion(d.getOccasion())
                .build();
    }
}
