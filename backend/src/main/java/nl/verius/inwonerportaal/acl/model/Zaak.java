package nl.verius.inwonerportaal.acl.model;

import java.time.LocalDate;

/**
 * Zaak/dossier, afgeleid van X-Works zaakgericht-werken (ZS/ZKN). Inzage voor de burger.
 */
public record Zaak(
        String zaaknummer,
        String omschrijving,
        String status,
        LocalDate startdatum
) {}
