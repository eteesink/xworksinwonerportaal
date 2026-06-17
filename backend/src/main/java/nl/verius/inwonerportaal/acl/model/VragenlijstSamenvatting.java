package nl.verius.inwonerportaal.acl.model;

/**
 * Korte aanduiding van een beschikbare vragenlijst in de catalogus.
 *
 * <p>In X-Works komt deze lijst uit een query over de {@code VRAGENLIJSTTEMPLATE}-entiteit
 * voor de betreffende {@code ADMINISTRATION} (gemeente).
 */
public record VragenlijstSamenvatting(
        String type,
        String titel
) {}
