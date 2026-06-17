package nl.verius.inwonerportaal.acl;

/** Gegooid wanneer een aanvraag niet bestaat of niet van de opgegeven burger is. */
public class AanvraagNietGevondenException extends RuntimeException {
    public AanvraagNietGevondenException(String aanvraagId) {
        super("Aanvraag niet gevonden: " + aanvraagId);
    }
}
