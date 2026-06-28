package nl.verius.inwonerportaal.i18n;

/**
 * Eén vertaalde waarde met herkomst/status.
 *
 * <p>Status: {@code source} (de Nederlandse bron) · {@code auto} (door de vertaalmotor gegenereerd,
 * nog niet gereviewd) · {@code reviewed} (door een beheerder vastgesteld; wordt niet door een
 * latere AI-run overschreven).
 */
public record Vertaling(String text, String status) {

    public static final String SOURCE = "source";
    public static final String AUTO = "auto";
    public static final String REVIEWED = "reviewed";
}
