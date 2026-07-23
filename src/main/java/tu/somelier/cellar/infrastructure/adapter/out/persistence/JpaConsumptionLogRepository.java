package tu.somelier.cellar.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import tu.somelier.cellar.infrastructure.adapter.out.persistence.entity.ConsumptionLogEntity;

import java.util.UUID;

public interface JpaConsumptionLogRepository extends JpaRepository<ConsumptionLogEntity, UUID> {}
