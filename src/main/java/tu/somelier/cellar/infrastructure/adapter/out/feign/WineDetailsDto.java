package tu.somelier.cellar.infrastructure.adapter.out.feign;

import java.util.UUID;

public record WineDetailsDto(
        UUID id,
        String name,
        String type,
        String region,
        String winery
) {}
