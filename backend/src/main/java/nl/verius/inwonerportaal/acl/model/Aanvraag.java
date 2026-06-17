package nl.verius.inwonerportaal.acl.model;

import java.util.List;
import java.util.Map;

/**
 * Een lopende of ingediende aanvraag/vragenlijst van de burger.
 *
 * @param status {@code concept} | {@code ingediend} | {@code ondertekend} (X-Works {@code DM_INGEDIEND})
 * @param zaaknummer door X-Works toegekend na indienen; {@code null} zolang concept
 */
public record Aanvraag(
        String id,
        String type,
        String titel,
        String status,
        Map<String, Object> antwoorden,
        List<Bijlage> bijlagen,
        String zaaknummer
) {}
