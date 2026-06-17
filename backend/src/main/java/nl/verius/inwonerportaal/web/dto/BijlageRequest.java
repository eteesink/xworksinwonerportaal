package nl.verius.inwonerportaal.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Metadata van een toe te voegen bijlage. (In deze opzet wordt alleen metadata verstuurd;
 * de echte bestandsupload volgt bij de implementatie tegen de X-Works documentenlaag/DCR.)
 */
public record BijlageRequest(
        @NotBlank(message = "Bestandsnaam is verplicht")
        String bestandsnaam,
        @Positive(message = "Grootte moet groter dan 0 zijn")
        long grootte
) {}
