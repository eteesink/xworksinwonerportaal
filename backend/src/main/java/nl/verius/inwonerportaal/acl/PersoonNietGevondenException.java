package nl.verius.inwonerportaal.acl;

/** Gegooid wanneer voor een BSN geen PERSOON in X-Works bestaat. */
public class PersoonNietGevondenException extends RuntimeException {
    public PersoonNietGevondenException(String bsn) {
        super("Geen persoon gevonden voor BSN " + bsn);
    }
}
