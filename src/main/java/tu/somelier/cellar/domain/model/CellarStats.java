package tu.somelier.cellar.domain.model;

import java.math.BigDecimal;
import java.util.Map;

public record CellarStats(
        int totalBottles,
        BigDecimal totalValue,
        BigDecimal avgBottlePrice,
        Map<String, Integer> byType,
        Map<String, Integer> byRegion,
        int readyToDrink,
        int expiringSoon
) {}
