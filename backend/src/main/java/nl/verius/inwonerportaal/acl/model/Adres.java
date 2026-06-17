package nl.verius.inwonerportaal.acl.model;

/**
 * Adres, afgeleid van X-Works entiteit ADRES.
 *
 * @param soort adressoort conform X-Works {@code CD_ADRESSOORT}: {@code G} = verblijf/woon,
 *              {@code C} = correspondentie.
 */
public record Adres(
        String straat,
        String huisnummer,
        String postcode,
        String woonplaats,
        String soort
) {}
