package nl.verius.inwonerportaal.acl.model;

import java.util.List;

/**
 * Metadata-definitie van een vragenlijst/aanvraagformulier.
 *
 * <p>Equivalent van de X-Works {@code vragenlijstTemplate} die {@code lsd-generate-form.xslt}
 * dynamisch rendert. De frontend rendert de velden generiek op basis van deze definitie.
 */
public record VragenlijstDefinitie(
        String type,
        String titel,
        List<VraagDefinitie> vragen
) {}
