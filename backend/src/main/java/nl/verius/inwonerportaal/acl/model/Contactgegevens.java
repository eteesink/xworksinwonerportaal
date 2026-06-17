package nl.verius.inwonerportaal.acl.model;

/**
 * Contactgegevens, afgeleid van X-Works entiteit CONTACTGEGEVENS.
 *
 * <p>In X-Works staan deze als losse occurrences met {@code CD_TAG}
 * ({@code CONTACT-TELEFOON}, {@code CONTACT-MAIL}) en {@code VC_VALUE}. Dit is het enige
 * persoonsblok dat in het oude portaal door de burger zelf bewerkbaar is.
 */
public record Contactgegevens(
        String telefoon,
        String email
) {}
