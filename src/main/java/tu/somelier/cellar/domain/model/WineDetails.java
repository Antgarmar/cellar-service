package tu.somelier.cellar.domain.model;

import java.util.UUID;

public record WineDetails(
        UUID id,
        String name,
        String type,
        String region,
        String winery
) {}
