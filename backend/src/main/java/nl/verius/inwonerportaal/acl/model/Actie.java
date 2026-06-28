package nl.verius.inwonerportaal.acl.model;

/** Een actie onder een subdoel (eenmalig of herhalend). */
public record Actie(String id, String omschrijving, String type, boolean gereed) {

    public static final String EENMALIG = "eenmalig";
    public static final String HERHALEND = "herhalend";
}
