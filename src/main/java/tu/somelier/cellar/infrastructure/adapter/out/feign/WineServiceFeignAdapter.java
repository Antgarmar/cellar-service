package tu.somelier.cellar.infrastructure.adapter.out.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tu.somelier.cellar.domain.model.WineDetails;
import tu.somelier.cellar.domain.port.out.WineServicePort;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WineServiceFeignAdapter implements WineServicePort {

    private final WineServiceFeignClient feignClient;

    @Override
    public Optional<WineDetails> findWineById(UUID wineId) {
        try {
            WineDetailsDto dto = feignClient.getWineById(wineId);
            return Optional.ofNullable(dto).map(this::toDomain);
        } catch (Exception e) {
            log.warn("Could not fetch wine details for wineId={}: {}", wineId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Map<UUID, WineDetails> findWinesByIds(Collection<UUID> wineIds) {
        return wineIds.stream()
                .distinct()
                .flatMap(id -> findWineById(id).stream().map(d -> Map.entry(id, d)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private WineDetails toDomain(WineDetailsDto dto) {
        return new WineDetails(dto.id(), dto.name(), dto.type(), dto.region(), dto.winery());
    }
}
