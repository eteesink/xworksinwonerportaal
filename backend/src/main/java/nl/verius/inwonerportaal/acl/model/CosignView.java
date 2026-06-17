package nl.verius.inwonerportaal.acl.model;

import java.util.List;

/**
 * Wat de medeondertekenaar te zien krijgt na het openen van de uitnodigingslink (na DigiD-login).
 *
 * @param magTekenen      of de ingelogde gebruiker dit document mag ondertekenen
 * @param redenNietTekenen reden indien {@code magTekenen} false (bijv. BSN komt niet overeen, of verlopen)
 */
public record CosignView(
        String aanvraagId,
        String titel,
        String documentHash,
        String status,
        List<Handtekening> reedsGetekendDoor,
        boolean magTekenen,
        String redenNietTekenen
) {}
