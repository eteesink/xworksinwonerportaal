package nl.verius.inwonerportaal.i18n;

/**
 * Eén regel in de beheerinterface: de sleutel, de Nederlandse bron, de waarde in de doeltaal
 * en de status ({@code missing}/{@code auto}/{@code reviewed}/{@code source}).
 */
public record I18nEntry(String key, String bron, String waarde, String status) {
}
