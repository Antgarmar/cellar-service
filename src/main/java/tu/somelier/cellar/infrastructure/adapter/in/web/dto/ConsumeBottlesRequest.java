package tu.somelier.cellar.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ConsumeBottlesRequest(
        @NotNull @Min(1) Integer quantity,
        String occasion
) {}
