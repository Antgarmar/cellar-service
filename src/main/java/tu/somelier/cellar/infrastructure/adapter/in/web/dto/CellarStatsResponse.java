package tu.somelier.cellar.infrastructure.adapter.in.web.dto;

import tu.somelier.cellar.domain.model.CellarStats;

import java.math.BigDecimal;
import java.util.Map;

public record CellarStatsResponse(
        int totalBottles,
        BigDecimal totalValue,
        BigDecimal avgBottlePrice,
        Map<String, Integer> byType,
        Map<String, Integer> byRegion,
        int readyToDrink,
        int expiringSoon
) {
    public static CellarStatsResponse from(CellarStats stats) {
        return new CellarStatsResponse(
                stats.totalBottles(),
                stats.totalValue(),
                stats.avgBottlePrice(),
                stats.byType(),
                stats.byRegion(),
                stats.readyToDrink(),
                stats.expiringSoon()
        );
    }
}
