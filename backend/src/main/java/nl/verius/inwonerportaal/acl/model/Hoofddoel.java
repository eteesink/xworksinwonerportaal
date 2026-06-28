package nl.verius.inwonerportaal.acl.model;

import java.util.List;

/** Een hoofddoel binnen een plan, met subdoelen. Max. 5 subdoelen per hoofddoel. */
public record Hoofddoel(String id, String titel, List<Subdoel> subdoelen) {

    public static final int MAX_SUBDOELEN = 5;
}
