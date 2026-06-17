package nl.verius.inwonerportaal.acl.model;

/** Eén validatiefout uit de regelevaluatie. */
public record Validatiefout(
        String vraagId,
        String melding
) {}
