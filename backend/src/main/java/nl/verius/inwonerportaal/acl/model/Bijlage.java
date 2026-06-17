package nl.verius.inwonerportaal.acl.model;

/**
 * Bijlage bij een aanvraag, afgeleid van de X-Works bijlage-afhandeling
 * ({@code vragenlijst-dropzone-verwerk} / {@code removeAttachedFile}).
 */
public record Bijlage(
        String id,
        String bestandsnaam,
        long grootte
) {}
