package tu.somelier.cellar.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tu.somelier.cellar.domain.exception.CellarEntryNotFoundException;
import tu.somelier.cellar.domain.exception.InsufficientStockException;
import tu.somelier.cellar.domain.model.*;
import tu.somelier.cellar.domain.port.in.*;
import tu.somelier.cellar.domain.port.out.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CellarDomainService implements
        GetCellarUseCase,
        AddCellarEntryUseCase,
        GetCellarEntryUseCase,
        UpdateCellarEntryUseCase,
        DeleteCellarEntryUseCase,
        ConsumeBottlesUseCase,
        GetCellarStatsUseCase,
        GetDrinkNowUseCase,
        GetExpiringSoonUseCase {

    private final CellarEntryRepositoryPort entryRepository;
    private final ConsumptionLogRepositoryPort logRepository;
    private final CellarStatsCachePort statsCache;
    private final WineServicePort wineService;

    private static final int EXPIRING_SOON_DEFAULT_DAYS = 30;

    // -------------------------------------------------------------------------
    // GetCellarUseCase
    // -------------------------------------------------------------------------

    @Override
    public Page<CellarEntry> getCellar(UUID userId, Pageable pageable) {
        Page<CellarEntry> page = entryRepository.findByUserId(userId, pageable);
        return page.map(this::enrichWithWineDetails);
    }

    // -------------------------------------------------------------------------
    // AddCellarEntryUseCase
    // -------------------------------------------------------------------------

    @Override
    public CellarEntry addEntry(AddEntryCommand command) {
        CellarEntry entry = CellarEntry.builder()
                .userId(command.userId())
                .wineId(command.wineId())
                .quantity(command.quantity())
                .purchaseDate(command.purchaseDate())
                .purchasePrice(command.purchasePrice())
                .location(command.location())
                .drinkFrom(command.drinkFrom())
                .drinkUntil(command.drinkUntil())
                .personalNotes(command.personalNotes())
                .status(CellarEntryStatus.AVAILABLE)
                .build();

        CellarEntry saved = entryRepository.save(entry);
        statsCache.evictStats(command.userId());
        return enrichWithWineDetails(saved);
    }

    // -------------------------------------------------------------------------
    // GetCellarEntryUseCase
    // -------------------------------------------------------------------------

    @Override
    public CellarEntry getEntry(UUID id, UUID userId) {
        CellarEntry entry = entryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CellarEntryNotFoundException(id));
        return enrichWithWineDetails(entry);
    }

    // -------------------------------------------------------------------------
    // UpdateCellarEntryUseCase
    // -------------------------------------------------------------------------

    @Override
    public CellarEntry updateEntry(UUID id, UUID userId, UpdateEntryCommand command) {
        CellarEntry entry = entryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CellarEntryNotFoundException(id));

        if (command.quantity() != null) entry.setQuantity(command.quantity());
        if (command.location() != null) entry.setLocation(command.location());
        if (command.drinkFrom() != null) entry.setDrinkFrom(command.drinkFrom());
        if (command.drinkUntil() != null) entry.setDrinkUntil(command.drinkUntil());
        if (command.personalNotes() != null) entry.setPersonalNotes(command.personalNotes());
        if (command.status() != null) entry.setStatus(command.status());

        CellarEntry saved = entryRepository.save(entry);
        statsCache.evictStats(userId);
        return enrichWithWineDetails(saved);
    }

    // -------------------------------------------------------------------------
    // DeleteCellarEntryUseCase
    // -------------------------------------------------------------------------

    @Override
    public void deleteEntry(UUID id, UUID userId) {
        if (!entryRepository.existsByIdAndUserId(id, userId)) {
            throw new CellarEntryNotFoundException(id);
        }
        entryRepository.deleteByIdAndUserId(id, userId);
        statsCache.evictStats(userId);
    }

    // -------------------------------------------------------------------------
    // ConsumeBottlesUseCase
    // -------------------------------------------------------------------------

    @Override
    public ConsumptionLog consume(UUID entryId, UUID userId, ConsumeCommand command) {
        CellarEntry entry = entryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new CellarEntryNotFoundException(entryId));

        if (entry.getQuantity() < command.quantity()) {
            throw new InsufficientStockException(command.quantity(), entry.getQuantity());
        }

        entry.setQuantity(entry.getQuantity() - command.quantity());
        if (entry.getQuantity() == 0) {
            entry.setStatus(CellarEntryStatus.CONSUMED);
        }
        entryRepository.save(entry);

        ConsumptionLog log = ConsumptionLog.builder()
                .entryId(entryId)
                .userId(userId)
                .wineId(entry.getWineId())
                .quantity(command.quantity())
                .consumedAt(LocalDateTime.now())
                .occasion(command.occasion())
                .build();

        ConsumptionLog saved = logRepository.save(log);
        statsCache.evictStats(userId);
        return saved;
    }

    // -------------------------------------------------------------------------
    // GetCellarStatsUseCase
    // -------------------------------------------------------------------------

    @Override
    public CellarStats getStats(UUID userId) {
        return statsCache.getStats(userId).orElseGet(() -> {
            CellarStats computed = computeStats(userId);
            statsCache.saveStats(userId, computed);
            return computed;
        });
    }

    private CellarStats computeStats(UUID userId) {
        List<CellarEntry> available = entryRepository.findAvailableByUserId(userId);
        LocalDate today = LocalDate.now();

        int totalBottles = available.stream().mapToInt(CellarEntry::getQuantity).sum();

        BigDecimal totalValue = available.stream()
                .filter(e -> e.getPurchasePrice() != null)
                .map(e -> e.getPurchasePrice().multiply(BigDecimal.valueOf(e.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgBottlePrice = totalBottles > 0
                ? totalValue.divide(BigDecimal.valueOf(totalBottles), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int readyToDrink = (int) available.stream().filter(e -> e.isReadyToDrink(today)).count();
        int expiringSoon = (int) available.stream()
                .filter(e -> e.isExpiringSoon(today, EXPIRING_SOON_DEFAULT_DAYS))
                .count();

        // Enrich with wine details for byType / byRegion aggregation
        Set<UUID> wineIds = available.stream().map(CellarEntry::getWineId).collect(Collectors.toSet());
        Map<UUID, WineDetails> wineDetailsMap = wineService.findWinesByIds(wineIds);

        Map<String, Integer> byType = new LinkedHashMap<>();
        Map<String, Integer> byRegion = new LinkedHashMap<>();

        for (CellarEntry entry : available) {
            WineDetails details = wineDetailsMap.get(entry.getWineId());
            if (details != null) {
                if (details.type() != null) {
                    byType.merge(details.type(), entry.getQuantity(), Integer::sum);
                }
                if (details.region() != null) {
                    byRegion.merge(details.region(), entry.getQuantity(), Integer::sum);
                }
            }
        }

        return new CellarStats(totalBottles, totalValue, avgBottlePrice, byType, byRegion, readyToDrink, expiringSoon);
    }

    // -------------------------------------------------------------------------
    // GetDrinkNowUseCase
    // -------------------------------------------------------------------------

    @Override
    public List<CellarEntry> getDrinkNow(UUID userId) {
        LocalDate today = LocalDate.now();
        return entryRepository.findDrinkNow(userId, today).stream()
                .map(this::enrichWithWineDetails)
                .toList();
    }

    // -------------------------------------------------------------------------
    // GetExpiringSoonUseCase
    // -------------------------------------------------------------------------

    @Override
    public List<CellarEntry> getExpiringSoon(UUID userId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate until = today.plusDays(days);
        return entryRepository.findExpiringSoon(userId, today, until).stream()
                .map(this::enrichWithWineDetails)
                .toList();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private CellarEntry enrichWithWineDetails(CellarEntry entry) {
        try {
            wineService.findWineById(entry.getWineId())
                    .ifPresent(entry::setWineDetails);
        } catch (Exception ignored) {
            // Graceful degradation: wine-service unavailable
        }
        return entry;
    }
}
