package nl.verius.inwonerportaal.acl.model;

/**
 * Resultaat van het indienen van een aanvraag/vragenlijst.
 *
 * <p>In X-Works is dit het equivalent van de operatie {@code vragenlijst-submit} /
 * {@code zak-saveWizard}: X-Works genereert de sleutel en geeft een zaaknummer terug.
 */
public record AanvraagResultaat(
        String zaaknummer,
        String status
) {}
