package nl.verius.inwonerportaal.acl.model;

/**
 * Bankrekening, afgeleid van X-Works entiteit BANKREKENING. Inzage-only voor de burger.
 */
public record Bankrekening(
        String iban,
        String tenaamstelling
) {}
