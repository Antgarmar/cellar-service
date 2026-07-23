package tu.somelier.cellar.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tu.somelier.cellar.domain.model.ConsumptionLog;
import tu.somelier.cellar.domain.port.in.*;
import tu.somelier.cellar.infrastructure.adapter.in.web.dto.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cellar")
@RequiredArgsConstructor
public class CellarController {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final int EXPIRING_SOON_DAYS = 30;

    private final GetCellarUseCase getCellarUseCase;
    private final AddCellarEntryUseCase addCellarEntryUseCase;
    private final GetCellarEntryUseCase getCellarEntryUseCase;
    private final UpdateCellarEntryUseCase updateCellarEntryUseCase;
    private final DeleteCellarEntryUseCase deleteCellarEntryUseCase;
    private final ConsumeBottlesUseCase consumeBottlesUseCase;
    private final GetCellarStatsUseCase getCellarStatsUseCase;
    private final GetDrinkNowUseCase getDrinkNowUseCase;
    private final GetExpiringSoonUseCase getExpiringSoonUseCase;

    /** GET /cellar — bodega paginada */
    @GetMapping
    public Page<CellarEntryResponse> getCellar(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return getCellarUseCase.getCellar(userId, pageable).map(CellarEntryResponse::from);
    }

    /** POST /cellar — añadir vino */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CellarEntryResponse addEntry(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @Valid @RequestBody AddCellarEntryRequest request) {
        var command = new AddCellarEntryUseCase.AddEntryCommand(
                userId,
                request.wineId(),
                request.quantity(),
                request.purchaseDate(),
                request.purchasePrice(),
                request.location(),
                request.drinkFrom(),
                request.drinkUntil(),
                request.personalNotes()
        );
        return CellarEntryResponse.from(addCellarEntryUseCase.addEntry(command));
    }

    /** GET /cellar/stats — estadísticas */
    @GetMapping("/stats")
    public CellarStatsResponse getStats(@RequestHeader(USER_ID_HEADER) UUID userId) {
        return CellarStatsResponse.from(getCellarStatsUseCase.getStats(userId));
    }

    /** GET /cellar/drink-now — listos para beber */
    @GetMapping("/drink-now")
    public List<CellarEntryResponse> getDrinkNow(@RequestHeader(USER_ID_HEADER) UUID userId) {
        return getDrinkNowUseCase.getDrinkNow(userId).stream()
                .map(CellarEntryResponse::from).toList();
    }

    /** GET /cellar/expiring-soon — próximos a caducar */
    @GetMapping("/expiring-soon")
    public List<CellarEntryResponse> getExpiringSoon(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @RequestParam(defaultValue = "30") int days) {
        return getExpiringSoonUseCase.getExpiringSoon(userId, days).stream()
                .map(CellarEntryResponse::from).toList();
    }

    /** GET /cellar/{id} — detalle de entrada */
    @GetMapping("/{id}")
    public CellarEntryResponse getEntry(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PathVariable UUID id) {
        return CellarEntryResponse.from(getCellarEntryUseCase.getEntry(id, userId));
    }

    /** PUT /cellar/{id} — actualizar entrada */
    @PutMapping("/{id}")
    public CellarEntryResponse updateEntry(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCellarEntryRequest request) {
        var command = new UpdateCellarEntryUseCase.UpdateEntryCommand(
                request.quantity(),
                request.location(),
                request.drinkFrom(),
                request.drinkUntil(),
                request.personalNotes(),
                request.status()
        );
        return CellarEntryResponse.from(updateCellarEntryUseCase.updateEntry(id, userId, command));
    }

    /** DELETE /cellar/{id} — eliminar entrada */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PathVariable UUID id) {
        deleteCellarEntryUseCase.deleteEntry(id, userId);
    }

    /** POST /cellar/{id}/consume — registrar consumo */
    @PostMapping("/{id}/consume")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumptionLog consume(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody ConsumeBottlesRequest request) {
        var command = new ConsumeBottlesUseCase.ConsumeCommand(request.quantity(), request.occasion());
        return consumeBottlesUseCase.consume(id, userId, command);
    }
}
