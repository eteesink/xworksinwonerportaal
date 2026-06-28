package nl.verius.inwonerportaal.acl.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Integraal Plan van de burger (baseline Epic 10). Bundelt afspraken en hoofddoelen
 * (met subdoelen en acties).
 */
public record Plan(String id, String titel, String laatsteWijzigingDoor,
                   LocalDateTime laatsteWijzigingOp, List<Afspraak> afspraken,
                   List<Hoofddoel> hoofddoelen) {
}
