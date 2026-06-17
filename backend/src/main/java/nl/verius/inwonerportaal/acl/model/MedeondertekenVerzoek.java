package nl.verius.inwonerportaal.acl.model;

import java.util.List;

/**
 * Verzoek om een document mede te laten ondertekenen door een tweede persoon (de partner).
 *
 * <p>De {@code token} zit in de uitnodigingslink; de partner logt met DigiD in en landt direct op de
 * ondertekenpagina. {@code partnerBsn} (indien gezet) bindt de uitnodiging aan één specifieke persoon —
 * na DigiD-login moet de ingelogde BSN daarmee overeenkomen.
 *
 * @param documentHash hash van het exacte document; handtekeningen gelden op deze hash
 * @param status       {@code wacht-op-partner} | {@code volledig-getekend} | {@code verlopen}
 */
public record MedeondertekenVerzoek(
        String token,
        String aanvraagId,
        String titel,
        String documentHash,
        String partnerEmail,
        String partnerBsn,
        String status,
        String verlooptOp,
        List<Handtekening> handtekeningen
) {}
