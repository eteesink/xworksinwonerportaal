package nl.verius.inwonerportaal.acl;

/** Gegooid wanneer een medeonderteken-uitnodiging niet bestaat, verlopen of al gebruikt is. */
public class UitnodigingNietGeldigException extends RuntimeException {
    public UitnodigingNietGeldigException(String melding) {
        super(melding);
    }
}
