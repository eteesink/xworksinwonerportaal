package nl.verius.inwonerportaal.acl.model;

import java.time.LocalDate;

/**
 * Een afspraak binnen een plan.
 *
 * @param locatie    bv. "Thuis", "Bel", "Bij de gemeente", "Op locatie"
 * @param herkomst   "consulent" | "inwoner" | "leverancier" | "interne-leverancier"
 * @param aanpasbaar of de inwoner deze afspraak mag wijzigen (leverancier-afspraken niet)
 */
public record Afspraak(String id, String titel, LocalDate datum, String van, String tot,
                       String locatie, String met, String herkomst, boolean aanpasbaar) {

    public static final String VAN_CONSULENT = "consulent";
    public static final String VAN_INWONER = "inwoner";
    public static final String VAN_LEVERANCIER = "leverancier";
    public static final String VAN_INTERNE_LEVERANCIER = "interne-leverancier";
}
